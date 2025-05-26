import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.example.myapplication.ui.theme.dataclass.RetrofitClient
import com.example.myapplication.ui.theme.dataclass.HelloResponse
import android.util.Base64

class MainViewModel : ViewModel() {

    private val _helloText = mutableStateOf("")
    val helloText: State<String> = _helloText

    fun fetchData() {
        viewModelScope.launch {
            try {
                val credentials = "testuser:testpass"
                val authHeader = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

                val response = RetrofitClient.api.getHello(authHeader)
                if (response.isSuccessful) {
                    val result: HelloResponse? = response.body()
                    _helloText.value = result?.message ?: "응답이 비어있어요."
                } else {
                    _helloText.value = "서버 오류: ${response.code()}"
                }
            } catch (e: Exception) {
                _helloText.value = "예외 발생: ${e.localizedMessage ?: "알 수 없는 오류"}"
                e.printStackTrace()
            }
        }
    }
}

