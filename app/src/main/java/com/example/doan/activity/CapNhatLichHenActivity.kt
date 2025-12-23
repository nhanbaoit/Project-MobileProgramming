package com.example.doan.activity

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dd1_doan_lan4.Appointment
import com.example.dd1_doan_lan4.DatabaseHelper
import com.example.doan.R
import com.example.doan.model.InforPet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CapNhatLichHenActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var appointmentId: Int = -1

    // View
    private lateinit var spinnerPet: Spinner
    private lateinit var spinnerService: Spinner
    private lateinit var spinnerDoctor: Spinner
    private lateinit var calendarView: CalendarView
    private lateinit var lvTimeSlots: ListView
    private lateinit var tvSelectedDateTime: TextView
    private lateinit var switchRepeat: SwitchMaterial
    private lateinit var btnUpdate: Button
    private lateinit var chkStatus: CheckBox
    private lateinit var bottomNav: BottomNavigationView

    // Data
    private var selectedCalendar: Calendar = Calendar.getInstance()
    private var selectedPet: InforPet? = null
    private var petList: List<InforPet> = emptyList()
    private var selectedTimeStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cap_nhat_lich_hen)
        if(!isAdmin()){
            return
        }
        appointmentId = intent.getIntExtra("APPOINTMENT_ID", -1)
        if (appointmentId == -1) {
            Toast.makeText(this, "Lỗi ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        initView()
        setControl()
        setEvent()
    }

    private fun initView() {
        spinnerPet = findViewById(R.id.spinnerPetSelector)
        spinnerService = findViewById(R.id.spinnerServiceType)
        spinnerDoctor = findViewById(R.id.spinnerDoctor)
        calendarView = findViewById(R.id.calendarViewMini)
        lvTimeSlots = findViewById(R.id.lvTimeSlots)
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime)
        switchRepeat = findViewById(R.id.switchRepeat)
        btnUpdate = findViewById(R.id.btnUpdate)
        bottomNav = findViewById(R.id.bottomNav)
        chkStatus = findViewById(R.id.chkStatus)
    }

    private fun setControl() {
        val oldAppt = dbHelper.getAppointmentById(appointmentId)

        if (oldAppt == null) {
            Toast.makeText(this, "Không tìm thấy lịch hẹn!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 1) Set ngày giờ
        selectedCalendar.timeInMillis = oldAppt.dateTime
        calendarView.date = oldAppt.dateTime

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        selectedTimeStr = timeFormat.format(selectedCalendar.time)

        updateDateTimeText()
        loadAvailableTimeSlots(selectedCalendar.get(Calendar.DAY_OF_MONTH))

        // 2) Set Spinner service/doctor
        setupStaticSpinner(
            spinnerService,
            listOf("Khám tổng quát", "Tiêm phòng", "Phẫu thuật", "Spa/Làm đẹp"),
            oldAppt.serviceType
        )
        setupStaticSpinner(
            spinnerDoctor,
            listOf("Bác sĩ Nam", "Bác sĩ Lan", "Phòng khám PetCare Q1"),
            oldAppt.doctorName
        )

        // 3) Set Switch + Status
        switchRepeat.isChecked = oldAppt.isRepeat

        // ✅ THÊM: set checkbox theo dữ liệu cũ
        chkStatus.isChecked = oldAppt.isStatus

        // 4) Set Pet (✅ SỬA: phải select theo oldAppt.pet_id, không phải petOwnerId)
        loadPetsAndSelectOld(oldAppt.pet_id)
    }

    private fun isAdmin(): Boolean{
        val sharedPreferences = getSharedPreferences("UserRole", MODE_PRIVATE)
        val role = sharedPreferences.getString("ROLE", "USER") // Mặc định là USER

        if(role =="USER"){
            Toast.makeText(this,"Bạn không có quyền vào đây", Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        return true
    }
    private fun loadPetsAndSelectOld(oldPetId: Int) {
        petList = dbHelper.getAllPets()
        val petNames = petList.map { "${it.title} - ${it.breed}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, petNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPet.adapter = adapter

        val index = petList.indexOfFirst { it.id == oldPetId }
        if (index != -1) {
            spinnerPet.setSelection(index)
            selectedPet = petList[index]
        } else if (petList.isNotEmpty()) {
            spinnerPet.setSelection(0)
            selectedPet = petList[0]
        }
    }

    private fun setEvent() {
        spinnerPet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                if (petList.isNotEmpty()) selectedPet = petList[pos]
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        calendarView.setOnDateChangeListener { _, year, month, day ->
            selectedCalendar.set(year, month, day)
            selectedTimeStr = null
            updateDateTimeText()
            loadAvailableTimeSlots(day)
        }

        lvTimeSlots.setOnItemClickListener { parent, _, pos, _ ->
            selectedTimeStr = parent.getItemAtPosition(pos) as String
            updateDateTimeText()
        }

        btnUpdate.setOnClickListener { processUpdate() }
    }

    private fun processUpdate() {
        if (selectedPet == null) {
            Toast.makeText(this, "Chưa có thú cưng!", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedTimeStr == null) {
            Toast.makeText(this, "Vui lòng chọn giờ!", Toast.LENGTH_SHORT).show()
            return
        }

        val parts = selectedTimeStr!!.split(":")
        selectedCalendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
        selectedCalendar.set(Calendar.MINUTE, parts[1].toInt())

        val updatedAppt = Appointment(
            id = appointmentId,
            petOwnerId = selectedPet!!.id,
            pet_id = selectedPet!!.id,
            petName = selectedPet!!.title,
            serviceType = spinnerService.selectedItem.toString(),
            doctorName = spinnerDoctor.selectedItem.toString(),
            dateTime = selectedCalendar.timeInMillis,
            isRepeat = switchRepeat.isChecked,

            // ✅ THÊM: LƯU TRẠNG THÁI
            isStatus = chkStatus.isChecked
        )

        dbHelper.updateAppointment(updatedAppt)
        Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateDateTimeText() {
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCalendar.time)
        tvSelectedDateTime.text = "$dateStr lúc ${selectedTimeStr ?: "..."}"
    }

    private fun loadAvailableTimeSlots(day: Int) {
        val slots = if (day % 2 == 0) listOf("07:45", "08:45", "09:45", "10:45") else listOf("12:45","13:45","14:45","15:45")
        lvTimeSlots.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, slots)
        lvTimeSlots.choiceMode = ListView.CHOICE_MODE_SINGLE
    }

    private fun setupStaticSpinner(spinner: Spinner, data: List<String>, value: String) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val pos = data.indexOf(value)
        if (pos >= 0) spinner.setSelection(pos)
    }
}
