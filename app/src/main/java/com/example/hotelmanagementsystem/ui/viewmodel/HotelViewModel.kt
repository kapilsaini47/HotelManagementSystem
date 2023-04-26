package com.example.hotelmanagementsystem.ui.viewmodel

import android.app.Application
import android.app.DatePickerDialog
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.customers.CustomerModel
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.models.employee.EmployeeModel
import com.example.hotelmanagementsystem.models.paymentInfo.Payment
import com.example.hotelmanagementsystem.models.paymentInfo.PaymentModel
import com.example.hotelmanagementsystem.models.room.RoomMasters
import com.example.hotelmanagementsystem.models.room.RoomModelX
import com.example.hotelmanagementsystem.networkmanager.NetworkManager
import com.example.hotelmanagementsystem.repository.Repository
import com.example.hotelmanagementsystem.utils.Resource
import com.razorpay.RazorpayClient
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


class HotelViewModel(
    private val repository: Repository,
    private val networkManager: NetworkManager,
    app:Application
):AndroidViewModel(app) {

    val roomResponse: MutableLiveData<Resource<RoomModelX>> = MutableLiveData()
    private var room:RoomModelX? = null

    val addRoomResponse:MutableLiveData<Resource<RoomMasters>> = MutableLiveData()
    val deleteRoomResponse:MutableLiveData<Resource<RoomModelX>> = MutableLiveData()
    val updateRoomResponse:MutableLiveData<Resource<RoomModelX>> = MutableLiveData()
    val findRoomByIdResponse:MutableLiveData<Resource<RoomMasters>> = MutableLiveData()

    val employeeResponse: MutableLiveData<Resource<EmployeeModel>> = MutableLiveData()
    val addEmployeeResponse: MutableLiveData<Resource<Employee>> = MutableLiveData()
    private val deleteEmployeeResponse:MutableLiveData<Resource<EmployeeModel>> = MutableLiveData()
    val updateEmployeeResponse:MutableLiveData<Resource<Employee>> = MutableLiveData()

    val getAllBookedRoomResponse: MutableLiveData<Resource<CustomerModel>> = MutableLiveData()
    val addBookingResponse:MutableLiveData<Resource<Customer>> = MutableLiveData()
    val updateBookRoomResponse:MutableLiveData<Resource<Customer>> = MutableLiveData()
    val deleteBookRoomResponse:MutableLiveData<Resource<Customer>> = MutableLiveData()

    val paymentResponse:MutableLiveData<Resource<PaymentModel>> = MutableLiveData()
    val addPaymentResponse:MutableLiveData<Resource<Payment>> = MutableLiveData()

    val findCustomerResponse:MutableLiveData<Resource<CustomerModel>> = MutableLiveData()

    private val _diffDays = MutableLiveData<Int?>()
    val diffDays : LiveData<Int?>
    get() = _diffDays

    private val _orderId = MutableLiveData<String?>()
    val orderId : LiveData<String?>
    get() = _orderId

    private val _employeeId = MutableLiveData<String?>()
    val employeeId : LiveData<String?>
    get() = _employeeId

    init {
        getAllRooms()
    }

    private fun getAllRooms() = viewModelScope.launch {
        handleNetworkSafeGetAllRoomResponse()
    }

    fun getAllCustomer() = viewModelScope.launch {
        handleNetworkSafeGetAllCustomerResponse()
    }

    fun getAllEmployee() = viewModelScope.launch {
        handleNetworkSafeGetAllEmployeeResponse()
    }

    fun getAllPayments() = viewModelScope.launch {
        handleNetworkSafeGetAllPayment()
    }

    fun addPayment(payment: Payment) = viewModelScope.launch {
        handleNetworkSafeAddPayment(payment)
    }

    fun addRoom(roomMasters: RoomMasters) = viewModelScope.launch {
        handleNetworkSafeAddRoomResponse(roomMasters)
    }

    fun addEmployee(employee: Employee) = viewModelScope.launch {
        handleNetworkSafeAddEmployeeResponse(employee)
    }

    fun bookRoomWithAddCustomer(customer: Customer) = viewModelScope.launch {
        handleNetworkSafeBookRoomInAddCustomerResponse(customer)
    }

    fun deleteRoom(id:Int) = viewModelScope.launch {
        handleNetworkSafeDeleteRoomResponse(id)
    }

    fun deleteCustomer(id:Int) = viewModelScope.launch {
        handleNetworkSafeDeleteBookingDetails(id)
    }

    fun deleteEmployee(id: Int) = viewModelScope.launch {
        handleNetworkSafeDeleteEmployeeResponse(id)
    }

    fun updateRoom(id: Int,roomMasters: RoomMasters) = viewModelScope.launch {
        handleNetworkSafeUpdateRoomResponse(id,roomMasters)
    }

    fun updateBookingDetails(id: Int,customer: Customer) = viewModelScope.launch {
        handleNetworkSafeUpdateBookingDetails(id,customer)
    }

    fun updateEmployee(id: Int,employee: Employee) = viewModelScope.launch {
        handleNetworkSafeUpdateEmployee(id,employee)
    }

    fun findRoomById(id: Int) = viewModelScope.launch {
        handleNetworkSafeFindRoomById(id)
    }

    fun searchCustomerByName(name:String) = viewModelScope.launch {
        handleNetworkFindCustomerByName(name)
    }

    private suspend fun handleNetworkSafeGetAllRoomResponse(){
        roomResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.getAllRooms()
                roomResponse.postValue(handleAllRoomResponse(response))
            }else{
                roomResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> roomResponse.postValue(Resource.Error("Network Failure"))
                else -> roomResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleAllRoomResponse(response: Response<RoomModelX>):Resource<RoomModelX>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                if (room == null){
                    room = resultResponse
                }else{
                    val oldViews = room?.roomMastersList
                    val newViews = resultResponse?.roomMastersList
                    if (newViews != null) {
                        oldViews?.addAll(newViews)
                    }
                }
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())

    }


    private fun handleAllCustomerResponse(response: Response<CustomerModel>):Resource<CustomerModel>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())

    }

    private suspend fun handleNetworkSafeGetAllCustomerResponse(){
        getAllBookedRoomResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.getAllCustomer()
                getAllBookedRoomResponse.postValue(handleAllCustomerResponse(response))
            }else{
                getAllBookedRoomResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> getAllBookedRoomResponse.postValue(Resource.Error("Network Failure"))
                else -> getAllBookedRoomResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }


    private fun handleAllEmployeeResponse(response: Response<EmployeeModel>):Resource<EmployeeModel>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeGetAllEmployeeResponse(){
        employeeResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.getAllEmployee()
                employeeResponse.postValue(handleAllEmployeeResponse(response))
            }else{
                employeeResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> employeeResponse.postValue(Resource.Error("Network Failure"))
                else -> employeeResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleAddRoomResponse(response: Response<RoomMasters>):Resource<RoomMasters>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeAddRoomResponse(roomMasters: RoomMasters){
        addRoomResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.addRoom(roomMasters)
                addRoomResponse.postValue(handleAddRoomResponse(response))
            }else{
                addRoomResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> addRoomResponse.postValue(Resource.Error("Network Failure"))
                else -> addRoomResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleAddEmployeeResponse(response: Response<Employee>):Resource<Employee>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeAddEmployeeResponse(employee: Employee){
        addEmployeeResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.addEmployee(employee)
                addEmployeeResponse.postValue(handleAddEmployeeResponse(response))
            }else{
                addEmployeeResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> addEmployeeResponse.postValue(Resource.Error("Network Failure"))
                else -> addEmployeeResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleBookRoomInAddCustomerResponse(response: Response<Customer>):Resource<Customer>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeBookRoomInAddCustomerResponse(customer: Customer){
        addBookingResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.addCustomer(customer)
                addBookingResponse.postValue(handleBookRoomInAddCustomerResponse(response))
            }else{
                addBookingResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> addBookingResponse.postValue(Resource.Error("Network Failure"))
                else -> addBookingResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleDeleteRoomResponse(response: Response<RoomModelX>):Resource<RoomModelX>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeDeleteRoomResponse(id:Int){
        deleteRoomResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.deleteRoom(id)
                deleteRoomResponse.postValue(handleDeleteRoomResponse(response))
            }else{
                deleteRoomResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> deleteRoomResponse.postValue(Resource.Error("Network Failure"))
                else -> deleteRoomResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }


    private fun handleDeleteEmployeeResponse(response: Response<EmployeeModel>):Resource<EmployeeModel>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeDeleteEmployeeResponse(id:Int){
        deleteEmployeeResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.deleteEmployee(id)
                deleteEmployeeResponse.postValue(handleDeleteEmployeeResponse(response))
            }else{
                deleteEmployeeResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> deleteEmployeeResponse.postValue(Resource.Error("Network Failure"))
                else -> deleteEmployeeResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleUpdateRoomResponse(response: Response<RoomModelX>):Resource<RoomModelX>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeUpdateRoomResponse(id:Int,roomMasters: RoomMasters){
        updateRoomResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.updateRoom(id,roomMasters)
                updateRoomResponse.postValue(handleUpdateRoomResponse(response))
            }else{
                updateRoomResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> updateRoomResponse.postValue(Resource.Error("Network Failure"))
                else -> updateRoomResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleUpdateBookingDetails(response: Response<Customer>):Resource<Customer>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeUpdateBookingDetails(id:Int,customer: Customer){
        updateBookRoomResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.updateBookingDetails(id,customer)
                updateBookRoomResponse.postValue(handleUpdateBookingDetails(response))
            }else{
                updateBookRoomResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> updateBookRoomResponse.postValue(Resource.Error("Network Failure"))
                else -> updateBookRoomResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleDeleteBookingDetails(response: Response<Customer>):Resource<Customer>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeDeleteBookingDetails(id:Int){
        deleteBookRoomResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.deleteCustomer(id)
                deleteBookRoomResponse.postValue(handleDeleteBookingDetails(response))
            }else{
                deleteBookRoomResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> deleteBookRoomResponse.postValue(Resource.Error("Network Failure"))
                else -> deleteBookRoomResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleFindRoomById(response: Response<RoomMasters>):Resource<RoomMasters>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeFindRoomById(id:Int){
        findRoomByIdResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.findRoomById(id)
                findRoomByIdResponse.postValue(handleFindRoomById(response))
            }else{
                findRoomByIdResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> findRoomByIdResponse.postValue(Resource.Error("Network Failure"))
                else -> findRoomByIdResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleUpdateEmployee(response: Response<Employee>):Resource<Employee>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeUpdateEmployee(id:Int,employee: Employee){
        updateEmployeeResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.updateEmployee(id,employee)
                updateEmployeeResponse.postValue(handleUpdateEmployee(response))
            }else{
                updateEmployeeResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> updateEmployeeResponse.postValue(Resource.Error("Network Failure"))
                else -> updateEmployeeResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    fun setDateInEditText(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(editText.context,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                editText.setText("$selectedYear/${selectedMonth + 1}/$selectedDay")
            }, year, month, day)

        datePickerDialog.show()

    }

    fun calculateAmount(startDate:String,endDate:String,amount:String){
        val dateFormat = SimpleDateFormat("yyyy/MM/dd")
        return try {
            val parseStartDate = dateFormat.parse(startDate)
            val parseEndDate = dateFormat.parse(endDate)

            val diff = parseEndDate?.time?.minus(parseStartDate?.time!!)
            val seconds = diff?.div(1000)
            val minutes = seconds?.div(60)
            val hours = minutes?.div(60)
            val days = hours?.div(24)
            val totalPay = days?.toInt()?.times(amount.toInt())
            _diffDays.postValue(totalPay)
        }catch (e:Exception){
            _diffDays.postValue(0)
        }
    }

    fun generateOrderId(billingAmount:TextView){
        val thread = Thread {
            try {
                val razorpay = RazorpayClient("rzp_test_QOZMYQ6cJ1Luo2", "vDkQFKEOhdHQ550qWpBaBLL7")
                val orderRequest = JSONObject()
                orderRequest.put("amount",(billingAmount.text.toString().toInt()*100).toString()) // amount in the smallest currency unit
                orderRequest.put("currency", "INR")
                val order= razorpay.orders.create(orderRequest)
                val jsonObject = JSONObject(order.toString())
                _orderId.postValue(jsonObject.getString("id"))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    fun generateEmployeeId(){
        //Generating unique employee id
        val empId = System.currentTimeMillis().toString() + Random.nextInt(1000, 9999)
        _employeeId.postValue(empId)
    }

    private fun handleGetAllPayment(response: Response<PaymentModel>):Resource<PaymentModel>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeGetAllPayment(){
        paymentResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.getAllPayments()
                paymentResponse.postValue(handleGetAllPayment(response))
            }else{
                paymentResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> paymentResponse.postValue(Resource.Error("Network Failure"))
                else -> paymentResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleAddPayment(response: Response<Payment>):Resource<Payment>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeAddPayment(payment: Payment){
        addPaymentResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.addPayment(payment)
                addPaymentResponse.postValue(handleAddPayment(response))
            }else{
                addPaymentResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> addPaymentResponse.postValue(Resource.Error("Network Failure"))
                else -> addPaymentResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun handleFindCustomerByName(response: Response<CustomerModel>):Resource<CustomerModel>{
        if (response.isSuccessful){
            response.body().let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message().toString())
    }

    private suspend fun handleNetworkFindCustomerByName(name: String){
        findCustomerResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<Application>().applicationContext)){
                val response = repository.findCustomerByName(name)
                findCustomerResponse.postValue(handleFindCustomerByName(response))
            }else{
                findCustomerResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (e:Throwable){
            when(e){
                is IOException -> findCustomerResponse.postValue(Resource.Error("Network Failure"))
                else -> findCustomerResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

}