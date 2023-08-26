echo "[+] Removing previously created files..."
rm -f ./output/implauncher.jar
rm -f ./output/implauncher.exe
rm -f ./output/implauncher-updater.jar
rm -f -R ./build
rm -f -R ./build-updater
echo "-----------------------------------------------------------"
echo "[+] Creating directories..."
mkdir -p -- ./output
mkdir ./build
mkdir ./build/self-updater
mkdir ./build-updater
echo "-----------------------------------------------------------"
echo "[+] Compiling updater..."
javac -d ./build-updater -source 1.8 -target 1.8 ./updater/*.java
echo "-----------------------------------------------------------"
echo "[+] Creating .jar from updater .class files..."
jar cvfe ./build/self-updater/implauncher-updater.jar updater.Main -C ./build-updater .
echo "-----------------------------------------------------------"
echo "[+] Copying library code into main build directory..."
unzip -o ./lib/jarchivelib-0.7.1-jar-with-dependencies.jar -d ./build
unzip -o ./lib/json-simple-1.1.1.jar -d ./build
unzip -o ./lib/pecoff4j-0.4.0.jar -d ./build
unzip -o ./lib/semver4j-3.1.0.jar -d ./build
rm -f ./build/META-INF/MANIFEST.MF
echo "-----------------------------------------------------------"
echo "[+] Compiling application..."
javac -d ./build -cp lib/jarchivelib-0.7.1-jar-with-dependencies.jar:lib/json-simple-1.1.1.jar:lib/pecoff4j-0.4.0.jar:lib/semver4j-3.1.0.jar -source 1.8 -target 1.8 ./src/*.java ./src/Setting/*.java
echo "-----------------------------------------------------------"
echo "[+] Creating .jar from application .class files..."
jar cvfm ./output/implauncher.jar manifest.txt -C ./build . -C . ./implauncher-data
chmod +x ./output/implauncher.jar
echo "-----------------------------------------------------------"
echo "[+] Creating application .exe wrapper..."
./bin/launch4j/launch4j ./config.l4j
echo "-----------------------------------------------------------"
echo "[+] Cleanup temp files and directories..."
rm -f -R ./build
rm -f -R ./build-updater
echo "-----------------------------------------------------------"
echo "[+] Done!"
echo "[+] Output: ./output/implauncher.jar"
echo "[+] Output: ./output/implauncher.exe"