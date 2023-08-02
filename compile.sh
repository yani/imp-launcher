rm -f implauncher.jar
rm -f -R ./build
mkdir ./build
unzip -o ./lib/jarchivelib-0.7.1-jar-with-dependencies.jar -d ./build
unzip -o ./lib/json-simple-1.1.1.jar -d ./build
unzip -o ./lib/pecoff4j-0.4.0.jar -d ./build
rm -f ./build/META-INF/MANIFEST.MF
javac -d ./build -cp lib/jarchivelib-0.7.1-jar-with-dependencies.jar:lib/json-simple-1.1.1.jar:lib/pecoff4j-0.4.0.jar -source 1.8 -target 1.8 *.java ./Settings/*.java
jar cvfm ./implauncher.jar manifest.txt -C ./build . -C . ./implauncher-data
rm -f -R ./build
chmod +x ./implauncher.jar

echo "-----------------------------------------------------------"
echo "Output: ./implauncher.jar"
echo ""
echo "JAR created!"