package com.example.doan.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dd1_doan_lan4.Appointment
import com.example.doan.R
import com.example.dd1_doan_lan4.DatabaseHelper
import com.example.doan.adapter.AppointmentSearchAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SearchAppointmentActivity : AppCompatActivity() {

    private lateinit var tvModeLabel: TextView

    private lateinit var layoutDateFilter: LinearLayout
    private lateinit var tvPickDate: TextView
    private lateinit var spnStatus: Spinner

    private lateinit var btnSearch: Button
    private lateinit var lvDanhSach: ListView

    private lateinit var layoutChiTiet: LinearLayout
    private lateinit var imgHinhPet: ImageView
    private lateinit var tvKetQua: TextView

    private lateinit var bottomNav: BottomNavigationView

    private lateinit var db: DatabaseHelper
    private val dsAppt = mutableListOf<Appointment>()
    private lateinit var apptAdapter: AppointmentSearchAdapter

    private var pickedDateMillis: Long? = null
    private val sdfDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_appointment)

        db = DatabaseHelper(this)

        setControl()
        setEvent()
        applyModeUI()
        loadInitial()
    }

    private fun setControl() {
        tvModeLabel = findViewById(R.id.tvModeLabel)

        layoutDateFilter = findViewById(R.id.layoutDateFilter)
        tvPickDate = findViewById(R.id.tvPickDate)
        spnStatus = findViewById(R.id.spnStatus)

        btnSearch = findViewById(R.id.btnSearch)
        lvDanhSach = findViewById(R.id.lvDanhSach)

        layoutChiTiet = findViewById(R.id.layoutChiTiet)
        imgHinhPet = findViewById(R.id.imgHinhPet)
        tvKetQua = findViewById(R.id.tvKetQua)

        bottomNav = findViewById(R.id.bottomNav)

        // ✅ custom adapter
        apptAdapter = AppointmentSearchAdapter(this, dsAppt)
        lvDanhSach.adapter = apptAdapter

        // ✅ spinner trạng thái (thêm lựa chọn "chưa chọn")
        val items = listOf("-- Chọn trạng thái --", "Chưa hoàn thành", "Đã hoàn thành")
        spnStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spnStatus.setSelection(0)

        layoutChiTiet.visibility = View.GONE
    }

    private fun applyModeUI() {
        tvModeLabel.text = "Đang tìm: Lịch khám (lọc theo ngày / trạng thái)"
        tvPickDate.text = "Chọn ngày (dd/MM/yyyy)"
    }

    private fun loadInitial() {
        // ✅ Mặc định show hết danh sách
        dsAppt.clear()
        dsAppt.addAll(db.getAllAppointments())
        apptAdapter.notifyDataSetChanged()
        layoutChiTiet.visibility = View.GONE
    }

    private fun showDatePicker(onPicked: (Long, String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d ->
                val dd = d.toString().padStart(2, '0')
                val mm = (m + 1).toString().padStart(2, '0')
                val label = "$dd/$mm/$y"

                val c2 = Calendar.getInstance()
                c2.set(y, m, d, 0, 0, 0)
                c2.set(Calendar.MILLISECOND, 0)
                onPicked(c2.timeInMillis, label)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setEvent() {

        tvPickDate.setOnClickListener {
            showDatePicker { millis, label ->
                pickedDateMillis = millis
                tvPickDate.text = label
            }
        }

        // ✅ Long click để bỏ lọc ngày
        tvPickDate.setOnLongClickListener {
            pickedDateMillis = null
            tvPickDate.text = "Chọn ngày (dd/MM/yyyy)"
            Toast.makeText(this, "Đã bỏ lọc ngày", Toast.LENGTH_SHORT).show()
            true
        }

        btnSearch.setOnClickListener {
            layoutChiTiet.visibility = View.GONE

            val dayMillis = pickedDateMillis // có thể null

            // -1: chưa chọn / tất cả | 0: chưa hoàn thành | 1: hoàn thành
            val statusFilter = when (spnStatus.selectedItemPosition) {
                1 -> 0
                2 -> 1
                else -> -1
            }

            dsAppt.clear()
            dsAppt.addAll(
                when {
                    // ✅ không chọn ngày, không chọn status -> tất cả
                    dayMillis == null && statusFilter == -1 ->
                        db.getAllAppointments()

                    // ✅ không chọn ngày, có chọn status -> lọc theo status
                    dayMillis == null && statusFilter != -1 ->
                        db.searchAppointmentsByStatus(statusFilter)

                    // ✅ có chọn ngày (status có thể tất cả/chưa/đã)
                    else ->
                        db.searchAppointmentsByDayAndStatus(dayMillis!!, statusFilter)
                }
            )
            apptAdapter.notifyDataSetChanged()

            if (dsAppt.isEmpty()) {
                Toast.makeText(this, "Không có lịch khám phù hợp!", Toast.LENGTH_SHORT).show()
            }
        }

        lvDanhSach.setOnItemClickListener { _, _, position, _ ->
            layoutChiTiet.visibility = View.VISIBLE
            val appt = dsAppt[position]

            imgHinhPet.setImageResource(R.drawable.banner_cat)

            val st = if (appt.isStatus) "✅ Hoàn thành" else "⏳ Chưa hoàn thành"

            tvKetQua.text =
                "LỊCH KHÁM #${appt.id}\n" +
                        "Thú cưng: ${appt.petName} (pet_id=${appt.pet_id})\n" +
                        "Dịch vụ: ${appt.serviceType}\n" +
                        "Bác sĩ: ${appt.doctorName}\n" +
                        "Thời gian: ${sdfDateTime.format(Date(appt.dateTime))}\n" +
                        "Lặp lại: ${if (appt.isRepeat) "Có" else "Không"}\n" +
                        "Trạng thái: $st"
        }

        bottomNav.selectedItemId = R.id.menu_timKiem
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_timKiem -> true

                R.id.menu_pet -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.menu_lichKham -> {
                    val intent = Intent(this, DatLichActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.menu_thongKe->{
                    val intent = Intent(this, MangHinhThongKe::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.menu_taiKhoan -> {
                    val intent = Intent(this, TaiKhoanActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // refresh lại list khi quay lại
        loadInitial()
        layoutChiTiet.visibility = View.GONE
    }
}
