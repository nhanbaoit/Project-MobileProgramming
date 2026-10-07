# 🐾 Ứng Dụng Quản Lý Phòng Khám Thú Cưng (Pet Care App)

Đây là ứng dụng di động trên tảng Android giúp quản lý phòng khám thú cưng, theo dõi hồ sơ y tế, và đặt lịch khám bệnh một cách tiện lợi. Dự án sử dụng kết hợp **SQLite** để lưu trữ cục bộ và **Firebase Firestore** để đồng bộ hóa dữ liệu thời gian thực.

## 🚀 Các Tính Năng Chính (Features)

### 1. Quản Trị Hệ Thống (Phân quyền User / Admin)
- Đăng ký và đăng nhập tài khoản.
- Quản lý quyền truy cập: Người dùng bình thường (`USER`) có giới hạn tính năng, Quản trị viên (`ADMIN`) có quyền truy cập toàn bộ hệ thống như Thống kê và Quản lý toàn bộ danh sách.

### 2. Quản Lý Hồ Sơ Thú Cưng (Pet Management)
- **Thêm/Sửa/Xóa** thông tin thú cưng (Tên, giống loài, cân nặng, hình ảnh, ghi chú).
- **Tìm kiếm** hồ sơ thú cưng dễ dàng (`SearchProfilePetActivity`).
- Dữ liệu thú cưng được đồng bộ trực tuyến thông qua `FirebasePetRepository`.

### 3. Đặt Lịch Hẹn Khám (Appointments)
- **Đặt lịch khám mới:** Chọn thú cưng, dịch vụ (Khám tổng quát, tiêm phòng, phẫu thuật, spa), chọn bác sĩ và thời gian.
- **Quản lý lịch hẹn:** Xem danh sách, cập nhật thông tin lịch hẹn hoặc tìm kiếm lịch hẹn.
- Dữ liệu lịch hẹn được lưu trữ tối ưu hóa thông qua cơ sở dữ liệu `SQLite` (`DatabaseHelper`).

### 4. Quản Lý Bệnh Án (Medical Records)
- Theo dõi chi tiết tình trạng sức khỏe của từng thú cưng.
- Thêm và cập nhật bệnh án (Triệu chứng, chuẩn đoán, ngày khám).
- Dữ liệu bệnh án được đồng bộ trực tuyến với Firestore (`FirebaseBenhAnRepository`).

### 5. Thống Kê & Báo Cáo (Statistics)
- Màn hình thống kê (`MangHinhThongKe`) dành cho Admin.
- Xem số lượng lịch hẹn, doanh thu hoặc tổng quan về các hoạt động của phòng khám.

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

- **Ngôn ngữ:** Kotlin
- **Nền tảng:** Android SDK (Min SDK 26, Target SDK 35)
- **Database:** 
  - [Firebase Firestore](https://firebase.google.com/docs/firestore) (Database NoSQL trên Cloud)
  - [SQLite](https://developer.android.com/training/data-storage/sqlite) (Database nội bộ Offline)
- **Kiến trúc mã nguồn:** Tổ chức theo mô hình MVC thu gọn (Models, Views/Activities, Repositories).
- **Giao diện:** XML Layouts, Material Design Components (Bottom Navigation, Switches, v.v.)

## 📂 Cấu Trúc Thư Mục Mẫu (Project Structure)

```text
app/src/main/java/com/example/doan/
├── activity/       # Chứa toàn bộ giao diện màn hình (UI/UX)
│   ├── MainActivity, DatLichActivity, CapNhatLichHenActivity, v.v.
├── adapter/        # Chức năng render danh sách (ListView, Spinner)
│   ├── CustomAdapterBenhAn, AppointmentListAdapter, v.v.
├── database/       # Quản lý cơ sở dữ liệu nội bộ SQLite
│   ├── DatabaseHelper, DBpet, DBThongKe
├── model/          # Định nghĩa cấu trúc dữ liệu (Data Classes)
│   ├── Appointment, InforPet, InforBenhAn
└── repository/     # Xử lý tương tác với Firebase Cloud Firestore
    ├── FirebasePetRepository, FirebaseBenhAnRepository
```

## ⚙️ Hướng Dẫn Cài Đặt (Installation)

1. Clone dự án về máy tính của bạn:
   ```bash
   git clone https://github.com/nhanbaoit/Project-MobileProgramming.git
   ```
2. Mở Android Studio và chọn **Open an existing Project**, trỏ tới thư mục `Project-MobileProgramming`.
3. Chờ Gradle đồng bộ hóa các thư viện (Sync Project with Gradle Files).
4. Thiết lập file `google-services.json`: 
   - Đảm bảo file cấu hình Firebase đã được đặt tại thư mục `app/google-services.json` để kết nối thành công với Firestore.
5. Cắm thiết bị Android thật (hoặc mở Emulator) và bấm **Run** (Shift + F10) để chạy ứng dụng.

## 🤝 Tác Giả (Contributors)
- **Phát triển bởi:** ngnhanbao (nhanbaoit)

---
*Bản tài liệu này được tạo tự động dựa trên mã nguồn của dự án!*
