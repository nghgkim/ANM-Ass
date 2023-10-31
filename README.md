# ANM-Ass
javac -d ./out ./srcs/main/java/hk231/rsa/*.java
java -cp ./out/ hk231.rsa.RSACryptoSystem
echo Main-Class: hk231.rsa.RSACryptoSystem > myManifest
jar cfm ./output.jar myManifest -C ./out/ .
java -jar  output.jar