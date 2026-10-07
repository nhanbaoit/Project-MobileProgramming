package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.model.Appointment
import com.example.doan.database.DatabaseHelper
import com.example.doan.R
import com.example.doan.model.InforPet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatLichActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spinnerPet: Spinner
    private lateinit var spinnerService: Spinner
    private lateinit var spinnerDoctor: Spinner
    private lateinit var calendarView: CalendarView
    private lateinit var lvTimeSlots: ListView
    private lateinit var tvSelectedDateTime: TextView
    private lateinit var switchRepeat: SwitchMaterial
    private lateinit var btnAdd: Button
    private lateinit var btnList: Button
    private lateinit var bottomNav: BottomNavigationView

    private var selectedCalendar: Calendar = Calendar.getInstance()
    private var selectedPet: InforPet? = null
    private var petList: List<InforPet> = emptyList()
    private var selectedTimeStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_dat_lich)

        dbHelper = DatabaseHelper(this)
        initView()
        setControl()
        setEvent()
        CheckUserRole()
    }

    private fun initView() {
        spinnerPet = findViewById(R.id.spinnerPetSelector)
        spinnerService = findViewById(R.id.spinnerServiceType)
        spinnerDoctor = findViewById(R.id.spinnerDoctor)
        calendarView = findViewById(R.id.calendarViewMini)
        lvTimeSlots = findViewById(R.id.lvTimeSlots)
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime)
        switchRepeat = findViewById(R.id.switchRepeat)
        btnAdd = findViewById(R.id.btnAdd)
        btnList = findViewById(R.id.btnList)
        bottomNav = findViewById(R.id.bottomNav)
    }

    private fun setControl() {
        loadAvailableTimeSlots(selectedCalendar.get(Calendar.DAY_OF_MONTH))

        val services = listOf("Khám tổng quát", "Tiêm phòng", "Phẫu thuật", "Spa/Làm đẹp")
        spinnerService.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, services)

        val doctors = listOf("Bác sĩ Nam", "Bác sĩ Lan", "Phòng khám PetCare Q1")
        spinnerDoctor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctors)

        loadPetsData()
    }

    override fun onResume() {
        super.onResume()
        loadPetsData()
    }

    private fun loadPetsData() {
        val petRepo = com.example.doan.repository.FirebasePetRepository()
        petRepo.listenToPets { list ->
            petList = list
            val petNames = petList.map { "${it.title} - ${it.breed}" }
            val adapter = ArrayAdapter(this@DatLichActivity, android.R.layout.simple_spinner_item, petNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPet.adapter = adapter
            if (petList.isNotEmpty()) selectedPet = petList[0]
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
    private fun setEvent() {
        spinnerPet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (petList.isNotEmpty()) selectedPet = petList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        calendarView.setOnDateChangeListener { _, year, month, day ->
            selectedCalendar.set(year, month, day)
            selectedTimeStr = null
            tvSelectedDateTime.text = "Ngày: $day/${month + 1} - Vui lòng chọn giờ"
            loadAvailableTimeSlots(day)
        }

        lvTimeSlots.setOnItemClickListener { parent, _, position, _ ->
            selectedTimeStr = parent.getItemAtPosition(position) as String
            tvSelectedDateTime.text = "Đã chọn: ${
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCalendar.time)
            } lúc $selectedTimeStr"
        }

        btnAdd.setOnClickListener { processBooking() }

        btnList.setOnClickListener {
            val intent = Intent(this, LichHenActivity::class.java)
            startActivity(intent)
        }

        bottomNav.selectedItemId = R.id.menu_lichKham
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_lichKham -> true

                R.id.menu_pet -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.menu_timKiem -> {
                    val intent = Intent(this, SearchProfilePetActivity::class.java)
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

    private fun processBooking() {
        if (selectedPet == null || selectedTimeStr == null) {
            Toast.makeText(this, "Thiếu thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        val parts = selectedTimeStr!!.split(":")
        selectedCalendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
        selectedCalendar.set(Calendar.MINUTE, parts[1].toInt())

        val newAppt = Appointment(
            petOwnerId = selectedPet!!.id,
            pet_id = selectedPet!!.id,
            petName = selectedPet!!.title,
            serviceType = spinnerService.selectedItem.toString(),
            doctorName = spinnerDoctor.selectedItem.toString(),
            dateTime = selectedCalendar.timeInMillis,
            isRepeat = switchRepeat.isChecked,

            // ✅ THÊM: mặc định chưa hoàn thành
            isStatus = false
        )

        val result = dbHelper.insertAppointment(newAppt)
        if (result > -1) showSuccessDialog()
        else Toast.makeText(this, "Lỗi lưu dữ liệu!", Toast.LENGTH_SHORT).show()
    }

    private fun showSuccessDialog() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(selectedCalendar.time)

        AlertDialog.Builder(this)
            .setTitle("🎉 Đặt lịch thành công!")
            .setMessage("Bé: ${selectedPet?.title}\nNgày: $dateStr\nGiờ: $selectedTimeStr")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                resetForm()
            }
            .setCancelable(false)
            .show()
    }

    private fun resetForm() {
        selectedTimeStr = null
        val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)
        tvSelectedDateTime.text = "Ngày: $day - Chọn giờ"
        loadAvailableTimeSlots(day)
    }

    private fun loadAvailableTimeSlots(day: Int) {
        val slots = if (day % 2 == 0) listOf("07:45", "08:45", "09:45", "10:45",)
        else listOf("13:45", "14:45", "15:45","16:45")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, slots)
        lvTimeSlots.adapter = adapter
        lvTimeSlots.choiceMode = ListView.CHOICE_MODE_SINGLE
    }
}
