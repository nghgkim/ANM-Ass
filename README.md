# ANM-Ass
## Thực thi chương trình
### Sử dụng `java` để chạy `.class` file
- Các file `.class` được tạo ra sau khi biên dịch sẽ được lưu trong thư mục `out/`. Để thực thi chương trình, ta cần thực hiện hai lệnh sau: 
```bash
javac -d ./out ./srcs/main/java/hk231/rsa/*.java

java -cp ./out/ hk231.rsa.RSACryptoSystem
```
### Tạo file `.jar`
Sau khi thực thi 2 lệnh trên, ta sẽ có các file `.class` trong thư mục `out/`. Để tạo file `.jar` để thực thi, ta cần tạo file `Manifest` chứa thông tin về `Main-Class` của chương trình.
```bash
echo Main-Class: hk231.rsa.RSACryptoSystem > myManifest

jar cfm ./output.jar myManifest -C ./out/ .

java -jar  output.jar
```
