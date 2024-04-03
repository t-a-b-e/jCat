# jCat

javaでmacOS14.1.1に搭載されたcatコマンドを模しました。  
(BSD版と呼ばれるものでしょうか)

使えるオプションは b, n, s です。  
  
### コンパイル：

```bash
javac jCat.java
```
classファイルが作成されます。  
  
### 実行：

classファイルがあるディレクトリで
```bash
java jCat [-bns] [file ...]
```

    

