# RSA 算法封装

---

### 综述

		该包用于实现对RSA算法的封装，目标是简化使用RSA算法加密解密简单数据的步骤。

### 示例代码

		RSAMaker maker = RSAMaker.newInstance(2048);
		RSAEncryptor encryptor = new RSAEncryptor(maker.getPublicKey());
		RSADecryptor decryptor = new RSADecryptor(maker.getPrivateKey());
		String str = "wanghui";
		String de = Base64.encode(encryptor.encrypt(str));
		System.out.println("原文： " + str);
		System.out.println("密文： " + de);
		System.out.println("解密： "
				+ decryptor.decryptToString(Base64.decode(de)));
		FileUtils.writeToFile("public.key",
				RSAUtils.toByteArray(encryptor.getPublicKey()));
		FileUtils.writeToFile("private.key",
				RSAUtils.toByteArray(decryptor.getPrivateKey()));

####　输出：

		原文： wanghui
		密文： RiqIcBjVWICje8rzV1wA49ZrLXMaPVcfw/TmUv6CFaq7+/xpHzS2nrXkcUvj2XIjEpzVOIH2fTCA
		4eyYTtPa7z/ohFWdy0JTsONbjF12ITkvjNwVKleEhSWBmhlcgNL3YgWa+sa48r5rV36sru+uJSXT
		6+TT6LxxEIrdknWQa4ypPlKaL8kwoKwMGY1TDly78f82fmvUJsaoqxqArAW5pPMWHiW87XBCw/JN
		78kfILtx7SV4KFqCyeA2QB9azjSBSo/RMoqa7oCOfoJOJapzgS2W4OvagWvWIABKBy10pKBY1InO
		c4MYx+5hyr2pZXs3ew5KpkZgJXKCE+gLad/lnQ==
		解密： wanghui

------		
### 主要类

- RSAMaker：	核心类，用于生成RSA密钥，也可以直接用于加密解密数据。
- RSAEncryptor：	加密类，传入一个RSA密钥后即可用于加密。
- RSADecryptor：	解密类，传入一个RSA密钥后即可用于解密。
- RSAUtils：	提供了若干方法用于完成 RSAKey 和 byte[] 的转换，方便存储密钥。
- FileUtils： 提供了两类方法用于从文件中读取byte[]数据，或者将byte[]直接写入文件。
- RSAPublicWorker： 使用RSA公钥完成加密或者解密工作。
- RSAPrivateWorker： 使用RSA私钥完成加密或者解密工作。
- IRSAWorker： 接口，定义了加密和解密的方法。
- AbstractRSAWorker： 抽象类，实现了IRSAWorker接口，并且完成了一些方法。 
- IRSAKey： 接口，定义了获取密钥或者模数/指数的方法。

### 使用流程（初次）
+ 使用RSAMaker 创建RSA密钥对
	+	RSAMaker maker = RSAMaker.newInstance(2048);
+ 保存RSA密钥对
	+	FileUtils.writeToFile("public.key",
				RSAUtils.toByteArray(encryptor.getPublicKey()));
	+	FileUtils.writeToFile("private.key",
				RSAUtils.toByteArray(decryptor.getPrivateKey()));
+ 创建RSA加密或者解密类
	+	RSAEncryptor encryptor = new RSAEncryptor(maker.getPublicKey());
	+	RSADecryptor decryptor = new RSADecryptor(maker.getPrivateKey());
+ 加密数据或者解密数据
	+	byte[] encryptedData = encryptor.encrypt( data );
	+	byte[] data = decryptor.decrypt( encryptedData );

### 使用流程（第二次）
+ 读取RSA密钥（公钥或者私钥）
	+	RSAEncryptor encryptor = RSAEncryptor.fromByteArray("public.key",
				RSAEncryptor.TYPE_PUBLIC);
	+	RSADecryptor decryptor = RSADecryptor.fromByteArray("private.key",
				RSADecryptor.TYPE_PRIVATE);
+ 加密或者解密数据
	+	

---
