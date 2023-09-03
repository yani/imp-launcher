package src;

import java.awt.*;

import java.io.ByteArrayInputStream;

import java.net.URI;
import java.net.http.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.*;

public class IndexPanel extends JPanel {

    JLabel loadingSpinner;

    JPanel newsPanel;
    JPanel workshopPanel;

    IndexPanel() {

        // Create initial panel
        super(new BorderLayout());

        try {
            // Setup a loading spinner
            ImageIcon spinner = new ImageIcon(this.getClass().getResource("/implauncher-data/spinner.gif"));
            this.loadingSpinner = new JLabel(spinner);
            this.add(loadingSpinner);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Setup workshop panel
        this.workshopPanel = new JPanel(new GridBagLayout());
        this.workshopPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        // "Latest workshop items" header
        JLabel workshopHeader = new JLabel("Latest workshop items");
        workshopHeader.setFont(new Font("Consolas", Font.BOLD, 18));
        workshopHeader.setBorder(BorderFactory.createEmptyBorder(15, 15, 7, 10));
        workshopHeader.setForeground(Color.WHITE);

        // Setup news panel
        RelativeLayout newsLayout = new RelativeLayout(RelativeLayout.Y_AXIS, 3);
        newsLayout.setAlignment(Component.CENTER_ALIGNMENT);
        this.newsPanel = new JPanel(newsLayout);
        this.newsPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

        // "KeeperFX News" header
        JLabel newsHeader = new JLabel("KeeperFX News");
        newsHeader.setFont(new Font("Consolas", Font.BOLD, 18));
        newsHeader.setBorder(BorderFactory.createEmptyBorder(15, 15, 7, 10));
        newsHeader.setForeground(Color.WHITE);
        this.newsPanel.add(newsHeader);

        // Start threads to grab info from website
        Thread newsThread = new Thread(() -> this.loadNews());
        newsThread.start();
        Thread latestWorkshopItemThread = new Thread(() -> this.loadLatestWorkshopItems());
        latestWorkshopItemThread.start();

        new Thread(() -> {

            // Wait for threads
            try {
                newsThread.join();
                latestWorkshopItemThread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            // Remove the loader when everything is loaded
            this.remove(this.loadingSpinner);

            // Create a main panel for the index content
            RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS, 5);
            rl.setAlignment(LEFT_ALIGNMENT);
            JPanel panel = new JPanel(rl);

            // Add stuff to main panel
            panel.add(workshopHeader);
            panel.add(this.workshopPanel);
            panel.add(newsHeader);
            panel.add(this.newsPanel);

            // Put into a scroll container
            JScrollPane containerScrollPane = new JScrollPane(panel);
            containerScrollPane.setBorder(null);
            containerScrollPane.getVerticalScrollBar().setUI(new ThemeBasicScrollBarUI());
            // containerScrollPane.getHorizontalScrollBar().setUI(new
            // ThemeBasicScrollBarUI());
            containerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            containerScrollPane.getVerticalScrollBar().setUnitIncrement(16);
            this.add(containerScrollPane);

            // Make sure interface is redrawn
            // (Otherwise the spinner GIF becomes static)
            this.repaint();
            this.revalidate();
        }).start();
    }

    public void loadLatestWorkshopItems() {

        int maxItems = 4;
        int currentItem = 1;
        int maxX = 2;

        GridBagConstraints itemGbConst = new GridBagConstraints();
        itemGbConst.gridx = 0;
        itemGbConst.gridy = 0;
        itemGbConst.anchor = GridBagConstraints.WEST;

        try {

            HttpClient imgHttpClient = HttpClient.newHttpClient();

            JSONObject json = HttpUtil
                    .getJsonObjectFromRestAPI(URI.create("https://keeperfx.net/api/v1/workshop/latest"));

            for (Object workshopItemObj : (JSONArray) json.get("workshop_items")) {

                // Convert article to a usable JSON object
                JSONObject item = (JSONObject) workshopItemObj;

                //////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////

                JPanel workshopItem = new JPanel(new BorderLayout(0, 10));
                workshopItem.setBackground(new Color(35, 35, 35));
                workshopItem.setBorder(BorderFactory.createEmptyBorder(8, 7, 8, 7));
                workshopItem.setPreferredSize(new Dimension(262, 100));

                GuiUtil.turnComponentIntoLink(workshopItem, (String) item.get("url"));

                //////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////

                // Get image
                String imageURL = (String) item.get("image");
                HttpRequest request = HttpRequest.newBuilder(new URI(imageURL)).build();
                HttpResponse<byte[]> response = imgHttpClient.send(request,
                        HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() != 200) {
                    System.out.println("Error loading workshop image: " + imageURL);
                    continue;
                }

                // Convert the image bytes to an Image object
                byte[] imageBytes = response.body();
                Image image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                image = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);

                // Create image label and add to item panel
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                workshopItem.add(imageLabel, BorderLayout.LINE_START);

                //////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////

                // Add infobox to the right
                RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS, 5);
                rl.setBorderGap(5);
                rl.setAlignment(RelativeLayout.LEADING);
                JPanel infoBox = new JPanel(rl);
                infoBox.setOpaque(false);
                infoBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
                workshopItem.add(infoBox, BorderLayout.CENTER);

                // Get title (and limit the size)
                String itemNameString = (String) item.get("name");
                if (itemNameString.length() > 19) {
                    itemNameString = itemNameString.substring(0, 17) + "...";
                }

                // Add title
                JLabel itemNameLabel = new JLabel(itemNameString);
                itemNameLabel.setFont(new Font("Consolas", Font.BOLD, 14));
                infoBox.add(itemNameLabel);

                // Add category
                String categoryString = (String) item.get("category");
                JLabel categoryLabel = new JLabel(WorkshopCategory.getConstantByString(categoryString));
                categoryLabel.setFont(new Font("Monospace", Font.PLAIN, 11));
                categoryLabel.setForeground(new Color(175, 175, 175));
                infoBox.add(categoryLabel);

                // Add date
                String dateString = (String) item.get("created_timestamp");
                JLabel dateLabel = new JLabel(dateString);
                dateLabel.setFont(new Font("Monospace", Font.PLAIN, 11));
                dateLabel.setForeground(new Color(100, 100, 100));
                infoBox.add(dateLabel);

                // Add submitter (user/creator/author)
                JSONObject submitterObject = (JSONObject) item.get("submitter");
                String usernameString = (String) submitterObject.get("username");
                JLabel usernameLabel = new JLabel(usernameString);
                usernameLabel.setFont(new Font("Monospace", Font.PLAIN, 11));
                usernameLabel.setForeground(new Color(255, 66, 23));
                infoBox.add(usernameLabel);

                //////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////

                // Add to main workshop items panel
                this.workshopPanel.add(workshopItem, itemGbConst);

                // Limit workshop items
                currentItem++;
                if (currentItem > maxItems) {
                    break;
                }

                // Set location in grid
                itemGbConst.gridx++;
                if (itemGbConst.gridx >= maxX) {
                    itemGbConst.gridx = 0;
                    itemGbConst.gridy++;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadNews() {

        try {
            JSONObject json = HttpUtil.getJsonObjectFromRestAPI(URI.create("https://keeperfx.net/api/v1/news/latest"));
            for (Object articleObj : (JSONArray) json.get("articles")) {

                // Convert article to a usable JSON object
                JSONObject article = (JSONObject) articleObj;

                // Get title (and limit the size)
                String titleString = (String) article.get("title");
                if (titleString.length() > 45) {
                    titleString = titleString.substring(0, 45) + "...";
                }

                // Get excerpt (and limit the size)
                String excerptString = (String) article.get("excerpt");
                if (excerptString.length() > 200) {
                    excerptString = excerptString.substring(0, 200) + "...";
                }

                // Create a Panel for this news item
                JPanel newsItem = new JPanel(new GridBagLayout());
                newsItem.setBackground(new Color(35, 35, 35));
                newsItem.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

                // Setup grid bag constants to order the panel
                GridBagConstraints gbConst = new GridBagConstraints();
                gbConst.gridy = 0;
                gbConst.anchor = GridBagConstraints.WEST;

                // News title
                JLabel newsTitle = new JLabel(titleString);
                newsTitle.setFont(new Font("Consolas", Font.BOLD, 16));
                newsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                newsTitle.setForeground(Color.WHITE);
                newsTitle.setMaximumSize(new Dimension(300, 50));

                // Add title
                newsItem.add(newsTitle, gbConst);

                // News excerpt
                JLabel newsExcerpt = new JLabel("<html><p style='width: 382px;'>" + excerptString + "</p></html>");
                newsExcerpt.setFont(new Font("Consolas", Font.PLAIN, 14));
                newsExcerpt.setVerticalAlignment(javax.swing.SwingConstants.TOP);
                newsExcerpt.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
                newsExcerpt.setForeground(Color.LIGHT_GRAY);

                // Add excerpt under title
                gbConst.gridy = 1;
                newsItem.add(newsExcerpt, gbConst);

                GuiUtil.turnComponentIntoLink(newsItem, (String) article.get("url"));

                // Add news item to news panel
                this.newsPanel.add(newsItem);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}