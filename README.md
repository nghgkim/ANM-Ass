# ANM-Ass
1.
`javac -d ./out ./srcs/main/java/hk231/rsa/*.java`
2.
`java -cp ./out/ hk231.rsa.RSACryptoSystem`
3.
`echo Main-Class: hk231.rsa.RSACryptoSystem > myManifest`
4.
`jar cfm ./output.jar myManifest -C ./out/ .`
5.
`java -jar  output.jar`