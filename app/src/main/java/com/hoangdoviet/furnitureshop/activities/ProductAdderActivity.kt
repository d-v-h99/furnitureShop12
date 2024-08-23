package com.hoangdoviet.furnitureshop.activities

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.hoangdoviet.furnitureshop.R
import com.hoangdoviet.furnitureshop.data.Product
import com.hoangdoviet.furnitureshop.databinding.ActivityProductAdderBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProductAdderActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProductAdderBinding.inflate(layoutInflater) }
    val selectedColors = mutableListOf<Int>()
    var selectedImages = mutableListOf<Uri>()
    val firestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog
                .Builder(this)
                .setTitle("Product color")
                .setPositiveButton("Select", object: ColorEnvelopeListener{
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)
                            updateColors()
                        }
                    }
                }).setNegativeButton("Cancel") {colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()
        }
        // ActivityResultContracts.StartActivityForResult() khởi chạy một hoạt động (activity) và xử lý kết quả trả về khi hoạt động đó hoàn thành.
        // Cụ thể trong trường hợp này, nó được dùng để chọn hình ảnh từ bộ nhớ của thiết bị và sau đó lưu trữ các URI (địa chỉ tài nguyên đồng nhất) của các hình ảnh đã chọn vào danh sách selectedImages.
        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
                //registerForActivityResult là một API mới trong Android giúp đơn giản hóa việc xử lý kết quả của các hoạt động (activities) mà bạn khởi chạy
                //ActivityResultContracts.StartActivityForResult() là một contract cho phép bạn khởi chạy một Intent để bắt đầu một hoạt động và nhận lại kết quả.
                //Biến result chứa thông tin về kết quả, bao gồm mã kết quả (resultCode) và dữ liệu (data).
                if(result.resultCode == Activity.RESULT_OK){ // k tra hđong trả về có thành công k
                    val intent = result.data // biến intent Lưu trữ Intent (chứa du liệu kq trả về từ hđong hoàn thành result.data)
                    // Chon nhieu
                    if(intent?.clipData != null){ //clipData là thuộc tính của Intent, chứa nhiều dữ liệu (dưới dạng ClipData).
                        val count = intent.clipData?.itemCount ?: 0 // nếu  toán tử elvis, đảm bảo rằng nếu clipData là null, giá trị trả về sẽ là 0.
                        (0 until count).forEach{
                            val imagesUri = intent.clipData?.getItemAt(it)?.uri
                            imagesUri?.let { selectedImages.add(it) }
                        }
                    }else{// chon 1 anh
                        val imageUri = intent?.data
                        imageUri?.let { selectedImages.add(it) }
                    }
                    updateImages()
                }
            }
        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)//, cho phép người dùng chọn nhiều hình ảnh cùng một lúc thay vì chỉ một hình ảnh.
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
            //CTION_GET_CONTENT là một hành động chuẩn trong Android, được sử dụng để yêu cầu người dùng chọn một mẩu nội dung từ một nguồn dữ liệu nào đó. Nó thường được dùng để mở trình chọn nội dung (content picker), cho phép người dùng chọn một tệp (ví dụ: hình ảnh, âm thanh, video) từ bộ nhớ của thiết bị hoặc từ các dịch vụ lưu trữ khác.
            //Khi sử dụng ACTION_GET_CONTENT, Android sẽ hiển thị một giao diện để người dùng chọn tệp từ các nguồn có sẵn (như thư viện hình ảnh, bộ nhớ ngoài, hoặc các ứng dụng quản lý tệp khác).
            //putExtra là một phương thức của Intent được sử dụng để gửi thêm dữ liệu kèm theo Intent.
        }
    }

    private fun updateImages() {
        binding.tvSelectedImages.setText(selectedImages.size.toString())
    }

    private fun updateColors() {
        var colors = ""
        selectedColors.forEach {
            colors = "$colors ${Integer.toHexString(it)}, "
        }
        binding.tvSelectedColors.text = colors
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.saveProduct){
            val productValidation = validateInformation()
            if(!productValidation){
                Toast.makeText(this, "Check your inputs", Toast.LENGTH_SHORT).show()
                return false
            }
            saveProducts()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveProducts() {
        val sizes = getSizesList(binding.edSizes.text.toString().trim())
        val imagesByteArrays = getImagesByteArrays() //7
        val name = binding.edName.text.toString().trim()
        val images = mutableListOf<String>()
        val category = binding.edCategory.text.toString().trim()
        val productDescription = binding.edDescription.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.edOfferPercentage.text.toString().trim()

        lifecycleScope.launch {
            showLoading()
            try {
                async {
                    imagesByteArrays.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imagesStorage = storage.child("products/images/$id")
                            val result = imagesStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }.await()
                //Trong hàm này, async được sử dụng để xử lý lưu trữ ảnh lên Firebase Storage. Mỗi ảnh sẽ được lưu trữ đồng thời thông qua launch trong async.
                //Chú ý: async trả về một giá trị Deferred, và bạn phải await để lấy kết quả hoặc để đảm bảo rằng tất cả các tác vụ con trong async hoàn thành trước khi tiếp tục.

                //launch là không chặn và không trả về kết quả, thích hợp cho các tác vụ không cần giá trị trả về.
                //async được sử dụng khi bạn cần trả về kết quả từ một coroutine, và có thể chờ kết quả bằng await.
            }catch (e: java.lang.Exception){
                hideLoading()
            }
            val product = Product(
                UUID.randomUUID().toString(),
                name,
                category,
                price.toFloat(),
                if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                if (productDescription.isEmpty()) null else productDescription,
                selectedColors,
                sizes,
                images
            )
            firestore.collection("Products").add(product).addOnSuccessListener {
                hideLoading()
            }.addOnFailureListener {
                Log.e("test2", it.message.toString())
                hideLoading()
            }
        }
    }
    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE

    }

    private fun getImagesByteArrays(): List<ByteArray>  {
        // chuyen dinh dang file hinh anh thanh mang byte
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            val stream = ByteArrayOutputStream() //stream đầu ra để lưu trữ dữ liệu dưới dạng mảng byte.
            //để lấy hình ảnh từ URI và chuyển nó thành đối tượng Bitmap.
            //contentResolver được dùng để truy cập dữ liệu được chỉ định bởi URI.
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
            //mageBmp.compress() là phương thức nén hình ảnh Bitmap thành định dạng JPEG và ghi dữ liệu nén vào stream.
            //85 là chất lượng nén, với giá trị từ 0 đến 100 (85 là chất lượng nén trung bình, giúp giảm kích thước file mà vẫn giữ được độ chi tiết).
            // nén ảnh thành công trả về true
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 85, stream)){
                val imageAsByteArray = stream.toByteArray() //huyển đổi dữ liệu trong ByteArrayOutputStream (đã chứa hình ảnh nén) thành một mảng byte (ByteArray).
                imagesByteArray.add(imageAsByteArray)
            }
        }
        return imagesByteArray
    }

    private fun getSizesList(sizes: String): List<String>? {
        if (sizes.isEmpty())
            return null
        val sizesList = sizes.split(",").map { it.trim() }
        return sizesList
    }

    private fun validateInformation(): Boolean {
        if(selectedImages.isEmpty()) return false
        if(binding.edName.text.toString().trim().isEmpty()) return false
        if(binding.edCategory.text.toString().trim().isEmpty()) return false
        if(binding.edPrice.text.toString().trim().isEmpty()) return false
        return true
    }
}