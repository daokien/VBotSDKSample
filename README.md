 Version 1.0.0

Thêm VBot SDK vào Project
•	Chuyển dạng xem từ Android  Project
 

•	Copy file “VBot-Android-SDK.aar”  “libs” 
 

•	Trong file “build.gradle(Module :app)”  “dependencies” thêm 
api fileTree(dir: 'libs', include: ['*.aar'])
dependencies {
    api fileTree(dir: 'libs', include: ['*.aar'])
}

•	Trong file “proguard-rules.pro” thêm
-keep class com.vpmedia.sdkvbot.** {*;}
-keep class org.pjsip.pjsua2.** {*;}
-keep class io.reactivex.** {*;}
-keep class retrofit2.** {*;}

Thêm firebase vào Project
•	Vào trang https://console.firebase.google.com
•	Tạo Project 
•	Thêm Ứng dụng của bạn vào Project vừa tạo
•	Trong “Project Setting”  “Service accounts”  “Firebase Admin SDK”  “Java”  “Generate new private key”
 
 
•	Thêm “SDK CREDENTIAL”:
{
  "type": "service_account",
  "project_id": "vbotsdk",
  "private_key_id": "d51d64e7c8d497f1c3f6dd8885a357efef8ae24d",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCiVsp+vAr3XS1Q\ndyJ5FFnco+0Y1DiiCHf0tbeQbjMTFfMocPqEtDCtbrYtRO/JdkzgHznVpd6yLhck\nG0b5ShB6Kt4A7n1NDBKTVPi9sjwqOYq5HEfPTnsofoIhixqMci2Qp+MSzEUilHp+\nEY7iQ/pMxtcXoqwkmbFoLJhiMsTuLI/8LW+1tex1pqSEFA9FUHD5EqoqE2QoUx6W\nQUdGpjXMaDJJKjtV2HftxD8DFqBYhJdJKo+T6mx2P6hKxyZWrWid/MICBKJnpwiU\nm1zk29PVB6i7m4ueyzcbaa8zL3WtMbifMJ+QZ7XrA/NzRzSFsRRVIz2QjftfUIga\nbWPdmcgrAgMBAAECggEASPoFG4XC1EJRvYtgttdcMiCK1A+KyeROsHYD5+xyD1Fd\ntr9bDs7F1yEeyijkNkd09Yd0A0QGYUjw5C1S8XvG/nRIylhrgXg83XhkPTpja4Pk\nhaRd76ZTHPgJ3bRfoeUt8IpYPLoRFiNzETspZTOz0FHOnXRmOCnTI4ZwlixGdN5h\nmN6rkXeAsNVh/O//w1dCYtSaMnr0M3/HMrHmUVqzFqen3oDnvh0OgrvQtwb9Q4n4\nN3tMk2OcVmwDvuZnvcGk3cOgneqf83xOt+lTIxStqSSIjVuA+p6EALDxto5XJCFr\n48kU6kkxIrZ44LifgGA9AG6Q+dN5cFcEq0MqktAvsQKBgQDczhzbZ+qRHdezhLxt\nexdnVLbifiydI7I+BUCGKgv7kbvPkMA+FyuTk/HMg365rUwvBnFBLfWVRw7yEC3E\nSwC8cNGrrviOO6q4uPAQy3uscfiUmuY2yMGsViJcvbxGlJAUliqwoigQoBpeD9zx\nNEz+ubvFisWcszI3D1iTZoyVwwKBgQC8NvyL20aC1WntlGa8oywVhQzufsjWGVpb\nMlJiCcfr5AQDo5Qjv2TcFJB0pWUifbqhKfjIPpdwo8p/D2/0bT66ajdTzC0S5L0P\nRHjl4+b9iAoenexBJ0VLRmVJ3E55QbFgj1Glqqt4LYT4z7dRsLduLD5mIKn4yArX\n6yYke8oVeQKBgGSTjhlysCoByer/n0TPOM3cjKGGj5PUWrBdfBKgvGyO6VmWubqO\nm07RqOZbTzIMehNBf/0Mjjy87VXdgLtU3rT3PspRHO7Mxur1coRz65WVJIGsPQjf\nUTjK00QjOMt9iFOYQ0HJ2y4Xf2sxFpbYF+o/eAR3PaQvC8SAvPwHTio5AoGAHvkp\njFZ0fxlhykAuvAJaQdxYaXMh1+HqNbHNTubvKImog3TK7ysTDQixURDa2Nc5/EWs\n9D8gqt+5djlzIhVMSl8qZbbnAAH2fum0v5hGrhhSGgHBHGO7Co02I/aGT3wZFZvg\nQtQYTbmn9U4xi9b6CL5tMl9TuWYqKqg+6agZdHkCgYA1+F49fQ6VcrmvJ4htfJYR\nlnd3l7ar+xcikAzN7uHV7tgpDpZl6NFUUFuH5CD/hcI4e/4cteBrHswtJb4LSuwL\nF3qd0y52a/OyWQnF8nrJ15zqmrZCgbkxAKuEZHjWmLs/oSNdq2NZI0NTpX0bD9cb\nidB4weQfo0zn8RhjujN5Rw==\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-my09j@vbotsdk.iam.gserviceaccount.com",
  "client_id": "110483888207240987486",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-my09j%40vbotsdk.iam.gserviceaccount.com",
  "universe_domain": "googleapis.com"
}


•	Thêm file “google-services.json” vào mục “app”
 

•	Trong file “build.gradle(Module :app)”  “dependencies” thêm
implementation platform('com.google.firebase:firebase-bom:32.4.0')
implementation 'com.google.firebase:firebase-messaging-ktx:23.3.1' 
mục “plugins” thêm “ id 'com.google.gms.google-services' ”
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.gms.google-services'
}

•	Trong file “build.gradle(Project)”  “plugins” thêm
id 'com.google.gms.google-services' version '4.4.0' apply false
plugins {
    id 'com.android.application' version '8.1.1' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.0' apply false
    id 'com.google.gms.google-services' version '4.4.0' apply false
}

•	Tạo class kế thừa “FirebaseMessagingService()” 
class FirebaseService : FirebaseMessagingService()
override fun onMessageReceived(remoteMessage: RemoteMessage) để nhận data gửi về
override fun onMessageReceived(remoteMessage: RemoteMessage) {
}
•	Trong file “manifests” mục “application” thêm services 
<service
    android:name=".FirebaseService"
    android:exported="false"
    android:stopWithTask="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>

Kết nối
•	Khởi tạo VBotClient
client = VBotClient(context)

•	Bắt đầu VBotClient
Khi đã đăng ký tài khoản sẽ tự động đăng ký lại tài khoản
client.startClient()

•	Kiểm tra VBotClient đã bắt đầu chưa
client.clientIsStart()

Ngắt kết nối
•	Dừng VbotClient
client.stopClient()

Lắng nghe sự kiện
•	Khởi tạo lắng nghe sự kiện
client.addListener(listener)
	
private var listener = object : ClientListener() {
    override fun onAccountRegistrationState(status: AccountRegistrationState, reason: String) {
    }

    override fun onCallState(state: CallState) {
    }

    override fun onErrorCode(erCode: Int) {
    }

}
o	onAccountRegistrationState: Lắng nghe sự kiện đăng ký tài khoản
	status
•	OK
•	Progress
•	Error
•	None
o	onCallState: Lắng nghe trạng thái của cuộc gọi
	state
•	Null
•	Calling
•	Outgoing
•	Incoming
•	Early
•	Connecting
•	Confirmed
•	Disconnected
o	onErrorCode: Lắng nghe lỗi
	erCode
•	BadRequest(400),
•	Forbidden(403),
•	SeverError(500),
•	WrongParam(-1),
•	Unknown(-1000),
•	timeout(-1001),
•	DataEmpty(-1002)

•	Xoá lắng nghe sự kiện
client.removeListener(listener)

 
Tài khoản
•	Đăng ký tài khoản
token: Token của user (Tạo tài khoản SDK để lấy token)
tokenFirebase: Firebase Token
client.registerAccount(token,tokenFirebase)

•	Lấy trạng thái tài khoản
client.getStateAccount()

•	Lấy tên tài khoản
client.getAccountUsername()

•	Đăng xuất Tài khoản
Khi đăng xuất sẽ không gửi thông báo cuộc gọi và xoá lưu trữ tài khoản (không tự động đăng ký tài khoản)
client.logout()

•	Huỷ đăng ký và xoá tài khoản 
client.unregisterAndDeleteAccount()

Hotline
•	Lấy danh sách hotline
client.getListHotline()

Trả về danh sách Hotline
o	listHotline: Dánh sách Hotline
	Name
	PhoneNumber

Gọi đến
•	Tạo cuộc gọi đến
transId: được trả về ở dữ liệu thông báo
client.addIncomingCall(transId)

•	Bắt đầu đổ chuông
client.startRinging()

•	Tắt cuộc gọi tới
Trạng thái tắt cuộc gọi
true: bận
false: không bận
client.declineIncomingCall(true)

•	Trả lời cuộc gọi
client.answerIncomingCall()

•	Dừng đổ chuông
client.checkAndStopLocalRingBackTone()

Gọi đi
•	Tạo cuộc gọi đi
o	hotline: số hotline
o	phone: số điện thoại
client.addOutgoingCall(hotline, phone)

Thao tác cuộc gọi
•	Kiểm tra cuộc gọi có tồn tại không
true: có cuộc gọi tồn tại
false: không có cuộc gọi 
client.isCall()

•	Bật/Tắt micro
true: bật
false: tắt
client.isMic(ismic)

•	Lấy giây cuộc gọi
client.getDuration()

•	Gửi DTMF
client.senDTMF(string)

•	Lấy tên người gọi đến
client.getRemoteAddressCall()

•	Tắt cuộc gọi
client.hangupCall()

•	Giữ cuộc gọi
true: giữ cuộc gọi
false: tiếp tục cuộc gọi
client.setHold(boolean)

