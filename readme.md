# 简介 #
app注册码工具，当app的试用时间到了之后，每次启动app时，都会自动关闭，需要用户注册后，才可以使用。

# 日志 #
## v1.0 ##
- 2018-06-02
- 初始导入

# Gradle #
1.root build.gradle

	`allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}`
	
2.app build.gradle

	dependencies {
	        implementation 'com.github.zhangliangming:Register:v1.1'
	}



# 混淆注意 #
-keep class com.zlm.libs.register.** { *; }


# 捐赠 #
如果该项目对您有所帮助，欢迎您的赞赏

- 微信

![](https://i.imgur.com/e3hERHh.png)

- 支付宝

![](https://i.imgur.com/29AcEPA.png)