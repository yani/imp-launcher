mkdir -p -- "./output"
rm -f ./output/implauncher.jar
rm -f ./output/implauncher.exe
rm -f -R ./build
mkdir ./build
unzip -o ./lib/jarchivelib-0.7.1-jar-with-dependencies.jar -d ./build
unzip -o ./lib/json-simple-1.1.1.jar -d ./build
unzip -o ./lib/pecoff4j-0.4.0.jar -d ./build
rm -f ./build/META-INF/MANIFEST.MF
javac -d ./build -cp lib/jarchivelib-0.7.1-jar-with-dependencies.jar:lib/json-simple-1.1.1.jar:lib/pecoff4j-0.4.0.jar -source 1.8 -target 1.8 ./src/*.java ./src/Setting/*.java
jar cvfm ./output/implauncher.jar manifest.txt -C ./build . -C . ./implauncher-data
rm -f -R ./build
chmod +x ./output/implauncher.jar
echo "-----------------------------------------------------------"
echo "JAR created!"
echo "Output: ./output/implauncher.jar"
echo "-----------------------------------------------------------"
./bin/launch4j/launch4j ./config
echo "-----------------------------------------------------------"
echo "EXE created!"
echo "Output: ./output/implauncher.exe"
echo "-----------------------------------------------------------"