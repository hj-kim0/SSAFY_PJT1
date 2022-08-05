import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todayfilm.BuildConfig.DEBUG
import com.example.todayfilm.databinding.FragmentCustomDialogBinding
import kotlinx.coroutines.NonCancellable.cancel

class CustomDialogFragment(): DialogFragment() {

    private var _binding: FragmentCustomDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        initDialog()
        return view
    }

    fun initDialog() {
        val btnBundle = arguments?.getStringArray("btnData")
        val duration = Toast.LENGTH_SHORT

        binding.completeDialogShowAll.setOnClickListener {
//            buttonClickListener. onButton1Clicked()
            Toast.makeText(context, "전체 공개로 설정되었습니다.", duration).show()


            dismiss()
        }
        binding.completeDialogShowFollowers.setOnClickListener {
            Toast.makeText(context, "팔로워에게 공개로 설정되었습니다.", duration).show()




            dismiss()
        }
        binding.completeDialogShowNobody.setOnClickListener {
            Toast.makeText(context, "비공개로 설정되었습니다.", duration).show()
//            buttonClickListener. onButton1Clicked()

            dismiss()
        }


}}
