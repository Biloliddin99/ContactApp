package com.example.contactapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.adapters.MyRvAdapter
import com.example.contactapp.databinding.ActivityMainBinding
import com.example.contactapp.helper.MyButton
import com.example.contactapp.helper.MySwipeHelper
import com.example.contactapp.listener.MyButtonClickListener
import com.example.contactapp.models.Contact
import com.github.florent37.runtimepermission.kotlin.askPermission

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MyRvAdapter
    private lateinit var contactList: ArrayList<Contact>
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.itemRv.setHasFixedSize(true)


        //add swipe
        val swipe = object : MySwipeHelper(this, binding.itemRv, 120) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                //add button
                buffer.add(
                    MyButton(this@MainActivity,
                        "Sms",
                        30,
                        R.drawable.ic_sms,
                        Color.parseColor("#FFDD2371"),
                        object : MyButtonClickListener {
                            override fun onClick(position: Int) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Id $position",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this@MainActivity, SmsActivity::class.java)
                                intent.putExtra("key", contactList[position])
                                startActivity(intent)
                            }
                        })
                )
                buffer.add(
                    MyButton(this@MainActivity,
                        "Call",
                        30,
                        R.drawable.ic_call,
                        Color.parseColor("#FFF8CA2A"),
                        object : MyButtonClickListener {
                            override fun onClick(position: Int) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Id $position",
                                    Toast.LENGTH_SHORT
                                ).show()
                                calling(position)
                            }
                        })
                )
            }

        }
        readContact()
    }

    private fun calling(position: Int) {

        askPermission(android.Manifest.permission.CALL_PHONE) {
            //all permissions already granted or just granted
            val phoneNumber = contactList[position].number
            val intent = Intent(Intent(Intent.ACTION_CALL))
            intent.data = Uri.parse("tel:$phoneNumber")

            startActivity(intent)
        }.onDeclined { e ->
            if (e.hasDenied()) {

                AlertDialog.Builder(this)
                    .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                    .setPositiveButton("yes") { dialog, which ->
                        e.askAgain();
                    } //ask again
                    .setNegativeButton("no") { dialog, which ->
                        dialog.dismiss();
                    }
                    .show();
            }

            if (e.hasForeverDenied()) {
                //the list of forever denied permissions, user has check 'never ask again'

                // you need to open setting manually if you really need it
                e.goToSettings();
            }
        }
    }

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    fun readContact() {
        contactList = ArrayList()
        askPermission(android.Manifest.permission.READ_CONTACTS) {
            //all permissions already granted or just granted
            val contacts = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null
            )
            while (contacts!!.moveToNext()) {
                val contact = Contact(
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
                )

                contactList.add(contact)
            }
            contacts.close()

            adapter = MyRvAdapter(binding.root.context, contactList)
            binding.itemRv.adapter = adapter
        }.onDeclined { e ->
            if (e.hasDenied()) {

                AlertDialog.Builder(this)
                    .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                    .setPositiveButton("yes") { dialog, which ->
                        e.askAgain();
                    } //ask again
                    .setNegativeButton("no") { dialog, which ->
                        dialog.dismiss();
                    }
                    .show();
            }

            if (e.hasForeverDenied()) {
                //the list of forever denied permissions, user has check 'never ask again'

                // you need to open setting manually if you really need it
                e.goToSettings();
            }
        }
    }

}