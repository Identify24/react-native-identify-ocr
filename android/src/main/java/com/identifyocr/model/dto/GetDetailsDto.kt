import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetDetailsDto(
    var modules:List<String>
)