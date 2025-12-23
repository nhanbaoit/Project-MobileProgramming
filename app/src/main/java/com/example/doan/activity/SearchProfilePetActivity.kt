package com.example.doan.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.adapter.BenhAnSearchAdapter
import com.example.doan.adapter.PetSearchAdapter
import com.example.doan.adapter.AppointmentSearchAdapter
import com.example.doan.database.DBBenhAn
import com.example.doan.database.DBpet
import com.example.doan.model.InforBenhAn
import com.example.doan.model.InforPet
import com.example.doan.R
import com.example.dd1_doan_lan4.DatabaseHelper
import com.example.dd1_doan_lan4.Appointment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SearchProfilePetActivity : AppCompatActivity() {

    private lateinit var spnLoaiTim: Spinner
    private lateinit var tvModeLabel: TextView

    private lateinit var edtSearch: EditText
    private lateinit var layoutKeyword: LinearLayout

    // Bệnh án filter
    private lateinit var layoutDateFilter: LinearLayout
    private lateinit var tvFromDate: TextView
    private lateinit var tvToDate: TextView
    private var fromDate: String? = null
    private var toDate: String? = null

    // ✅ Lịch hẹn filter
    private lateinit var layoutApptFilter: LinearLayout
    private lateinit var tvApptDate: TextView
    private lateinit var spnApptStatus: Spinner
    private var apptDayMillis: Long? = null

    private lateinit var btnSearch: Button
    private lateinit var lvDanhSach: ListView

    private lateinit var layoutChiTiet: LinearLayout
    private lateinit var imgHinhPet: ImageView
    private lateinit var tvKetQua: TextView

    private lateinit var dbPet: DBpet
    private lateinit var dbBenhAn: DBBenhAn

    // ✅ DB lịch hẹn (appointments)
    private lateinit var dbAppt: DatabaseHelper

    private val dsPet = mutableListOf<InforPet>()
    private val dsBenhAn = mutableListOf<InforBenhAn>()
    private val dsAppt = mutableListOf<Appointment>()

    private lateinit var petAdapter: PetSearchAdapter
    private lateinit var benhAnAdapter: BenhAnSearchAdapter
    private lateinit var apptAdapter: AppointmentSearchAdapter

    private lateinit var bottomNav: BottomNavigationView

    private fun isPetMode(): Boolean = spnLoaiTim.selectedItemPosition == 0
    private fun isBenhAnMode(): Boolean = spnLoaiTim.selectedItemPosition == 1
    private fun isApptMode(): Boolean = spnLoaiTim.selectedItemPosition == 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_profile_pet)

        dbPet = DBpet(this)
        dbBenhAn = DBBenhAn(this)
        dbAppt = DatabaseHelper(this)

        setControl()
        setEvent()
        applyModeUI()
        loadInitial()
        CheckUserRole()
    }

    private fun setControl() {
        spnLoaiTim = findViewById(R.id.spnLoaiTim)
        tvModeLabel = findViewById(R.id.tvModeLabel)

        layoutKeyword = findViewById(R.id.layoutKeyword)
        edtSearch = findViewById(R.id.edtSearch)

        layoutDateFilter = findViewById(R.id.layoutDateFilter)
        tvFromDate = findViewById(R.id.tvFromDate)
        tvToDate = findViewById(R.id.tvToDate)

        // ✅ lịch hẹn filter
        layoutApptFilter = findViewById(R.id.layoutApptFilter)
        tvApptDate = findViewById(R.id.tvApptDate)
        spnApptStatus = findViewById(R.id.spnApptStatus)

        btnSearch = findViewById(R.id.btnSearch)
        lvDanhSach = findViewById(R.id.lvDanhSach)

        layoutChiTiet = findViewById(R.id.layoutChiTiet)
        imgHinhPet = findViewById(R.id.imgHinhPet)
        tvKetQua = findViewById(R.id.tvKetQua)

        petAdapter = PetSearchAdapter(this, dsPet)
        benhAnAdapter = BenhAnSearchAdapter(this, dsBenhAn)

        // ✅ Custom adapter lịch hẹn
        apptAdapter = AppointmentSearchAdapter(this, dsAppt)

        lvDanhSach.adapter = petAdapter
        layoutChiTiet.visibility = View.GONE

        bottomNav = findViewById(R.id.bottomNav)

        // ✅ Spinner trạng thái lịch hẹn: "chưa chọn gì" -> không lọc
        val statusItems = listOf("-- Chọn trạng thái --", "Chưa hoàn thành", "Đã hoàn thành")
        spnApptStatus.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusItems)
        spnApptStatus.setSelection(0)
    }

    private fun applyModeUI() {
        layoutChiTiet.visibility = View.GONE

        when {
            isPetMode() -> {
                tvModeLabel.text = "Đang tìm: Thú cưng"

                layoutKeyword.visibility = View.VISIBLE
                edtSearch.isEnabled = true
                edtSearch.hint = "Nhập tên/giống thú cưng"

                layoutDateFilter.visibility = View.GONE
                fromDate = null
                toDate = null
                tvFromDate.text = "Từ ngày (dd/MM/yyyy)"
                tvToDate.text = "Đến ngày (dd/MM/yyyy)"

                layoutApptFilter.visibility = View.GONE
                apptDayMillis = null
                tvApptDate.text = "Chọn ngày (dd/MM/yyyy)"
                spnApptStatus.setSelection(0)

                lvDanhSach.adapter = petAdapter
            }

            isBenhAnMode() -> {
                tvModeLabel.text = "Đang tìm: Bệnh án (lọc theo thời gian)"

                layoutKeyword.visibility = View.VISIBLE
                edtSearch.setText("")
                edtSearch.isEnabled = false
                edtSearch.hint = "Bệnh án chỉ lọc theo thời gian"

                layoutDateFilter.visibility = View.VISIBLE

                layoutApptFilter.visibility = View.GONE
                apptDayMillis = null
                tvApptDate.text = "Chọn ngày (dd/MM/yyyy)"
                spnApptStatus.setSelection(0)

                lvDanhSach.adapter = benhAnAdapter
            }

            else -> {
                tvModeLabel.text = "Đang tìm: Lịch hẹn (lọc theo ngày / trạng thái)"

                layoutKeyword.visibility = View.INVISIBLE
                edtSearch.setText("")

                layoutDateFilter.visibility = View.GONE
                fromDate = null
                toDate = null
                tvFromDate.text = "Từ ngày (dd/MM/yyyy)"
                tvToDate.text = "Đến ngày (dd/MM/yyyy)"

                layoutApptFilter.visibility = View.VISIBLE
                lvDanhSach.adapter = apptAdapter
            }
        }
    }
    private fun CheckUserRole(){
        val sharedPreferences = getSharedPreferences("UserRole",MODE_PRIVATE)
        val role = sharedPreferences.getString("ROLE","USER") //mặc định là user
        if(role == "USER"){
            val menu = bottomNav.menu
            menu.findItem(R.id.menu_pet).isVisible=false
            menu.findItem(R.id.menu_thongKe).isVisible=false
        }
    }
    private fun loadInitial() {
        if (isPetMode()) {
            dsPet.clear()
            dsPet.addAll(dbPet.LayDL())
            petAdapter.notifyDataSetChanged()

        } else if (isBenhAnMode()) {
            dsBenhAn.clear()
            benhAnAdapter.notifyDataSetChanged()

        } else {
            // ✅ Lịch hẹn: mặc định show hết
            dsAppt.clear()
            dsAppt.addAll(dbAppt.getAllAppointments())
            apptAdapter.notifyDataSetChanged()
        }
    }

    private fun showDatePicker(onPicked: (String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d ->
                val dd = d.toString().padStart(2, '0')
                val mm = (m + 1).toString().padStart(2, '0')
                onPicked("$dd/$mm/$y")
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showDatePickerMillis(onPicked: (String, Long) -> Unit) {
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
                onPicked(label, c2.timeInMillis)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setEvent() {

        spnLoaiTim.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyModeUI()
                loadInitial()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // bệnh án date
        tvFromDate.setOnClickListener {
            if (isBenhAnMode()) {
                showDatePicker { picked ->
                    fromDate = picked
                    tvFromDate.text = picked
                }
            }
        }

        tvToDate.setOnClickListener {
            if (isBenhAnMode()) {
                showDatePicker { picked ->
                    toDate = picked
                    tvToDate.text = picked
                }
            }
        }

        // ✅ lịch hẹn date
        tvApptDate.setOnClickListener {
            if (isApptMode()) {
                showDatePickerMillis { label, millis ->
                    tvApptDate.text = label
                    apptDayMillis = millis
                }
            }
        }

        // (Tuỳ chọn) Long click để bỏ lọc ngày
        tvApptDate.setOnLongClickListener {
            if (isApptMode()) {
                apptDayMillis = null
                tvApptDate.text = "Chọn ngày (dd/MM/yyyy)"
                Toast.makeText(this, "Đã bỏ lọc ngày", Toast.LENGTH_SHORT).show()
                true
            } else false
        }

        btnSearch.setOnClickListener {
            layoutChiTiet.visibility = View.GONE

            when {
                isPetMode() -> {
                    val kw = edtSearch.text.toString().trim()
                    dsPet.clear()
                    dsPet.addAll(if (kw.isEmpty()) dbPet.LayDL() else dbPet.searchPet(kw))
                    petAdapter.notifyDataSetChanged()

                    if (dsPet.isEmpty()) {
                        Toast.makeText(this, "Không tìm thấy thú cưng phù hợp!", Toast.LENGTH_SHORT).show()
                    }
                }

                isBenhAnMode() -> {
                    val f = fromDate
                    val t = toDate
                    if (f.isNullOrBlank() || t.isNullOrBlank()) {
                        Toast.makeText(this, "Vui lòng chọn Từ ngày và Đến ngày", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    dsBenhAn.clear()
                    dsBenhAn.addAll(dbBenhAn.filterBenhAnByDateRange(f, t))
                    benhAnAdapter.notifyDataSetChanged()

                    if (dsBenhAn.isEmpty()) {
                        Toast.makeText(this, "Không có bệnh án trong khoảng thời gian này!", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    // ✅ Lịch hẹn: lọc NGÀY và TRẠNG THÁI độc lập
                    val statusFilter = when (spnApptStatus.selectedItemPosition) {
                        1 -> 0 // chưa hoàn thành
                        2 -> 1 // hoàn thành
                        else -> -1 // chưa chọn / tất cả
                    }

                    val dayMillis = apptDayMillis // có thể null

                    dsAppt.clear()
                    dsAppt.addAll(
                        when {
                            dayMillis == null && statusFilter == -1 ->
                                dbAppt.getAllAppointments()

                            dayMillis == null && statusFilter != -1 ->
                                dbAppt.searchAppointmentsByStatus(statusFilter)

                            else ->
                                dbAppt.searchAppointmentsByDayAndStatus(dayMillis!!, statusFilter)
                        }
                    )
                    apptAdapter.notifyDataSetChanged()

                    if (dsAppt.isEmpty()) {
                        Toast.makeText(this, "Không có lịch hẹn phù hợp!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lvDanhSach.setOnItemClickListener { _, _, position, _ ->
            layoutChiTiet.visibility = View.VISIBLE

            when {
                isPetMode() -> {
                    val pet = dsPet[position]
                    imgHinhPet.setImageResource(pet.image)
                    tvKetQua.text =
                        "PET#${pet.id}\n" +
                                "Tên: ${pet.title}\n" +
                                "Giống: ${pet.breed}\n" +
                                "Cân nặng: ${pet.weight} kg\n" +
                                "Ghi chú: ${pet.chitiet}"
                }

                isBenhAnMode() -> {
                    val ba = dsBenhAn[position]
                    val pet = dbPet.getpetID(ba.pet_id)

                    if (pet != null) {
                        imgHinhPet.setImageResource(pet.image)
                        tvKetQua.text =
                            "BỆNH ÁN #${ba.id}\n" +
                                    "Ngày khám: ${ba.ngay}\n" +
                                    "Triệu chứng: ${ba.trieuchung}\n" +
                                    "Chẩn đoán: ${ba.chuanDoan}\n" +
                                    "Thuốc: ${ba.thuoc}\n\n" +
                                    "THÚ CƯNG:\n" +
                                    "Tên: ${pet.title}\n" +
                                    "Giống: ${pet.breed}\n" +
                                    "Cân nặng: ${pet.weight} kg\n" +
                                    "Ghi chú: ${pet.chitiet}"
                    } else {
                        imgHinhPet.setImageResource(R.drawable.so_benh_an)
                        tvKetQua.text =
                            "BỆNH ÁN #${ba.id}\n" +
                                    "Ngày khám: ${ba.ngay}\n" +
                                    "Triệu chứng: ${ba.trieuchung}\n" +
                                    "Chẩn đoán: ${ba.chuanDoan}\n" +
                                    "Thuốc: ${ba.thuoc}\n\n"
                    }
                }

                else -> {
                    val appt = dsAppt[position]
                    imgHinhPet.setImageResource(R.drawable.banner_cat)

                    val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val st = if (appt.isStatus) "✅ Hoàn thành" else "⏳ Chưa hoàn thành"

                    tvKetQua.text =
                        "LỊCH HẸN #${appt.id}\n" +
                                "Thú cưng: ${appt.petName}\n" +
                                "Dịch vụ: ${appt.serviceType}\n" +
                                "Bác sĩ: ${appt.doctorName}\n" +
                                "Thời gian: ${fmt.format(Date(appt.dateTime))}\n" +
                                "Lặp lại: ${if (appt.isRepeat) "Có" else "Không"}\n" +
                                "Trạng thái: $st"
                }
            }
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
        loadInitial()
        layoutChiTiet.visibility = View.GONE
    }
}
