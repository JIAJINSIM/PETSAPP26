package com.example.petsapp26

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

enum class AdapterMode {
    ADMIN,
    USER
}

class AppointmentAdapter(
    context: Context,
    private val appointments: List<Appointment>,
    private val listener: AppointmentActionListener,
    private val mode: AdapterMode = AdapterMode.ADMIN  // Default mode
    ) : ArrayAdapter<Appointment>(context, 0, appointments) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        when (mode) {
            AdapterMode.ADMIN -> {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_item_appt, parent, false)
                val appointment = getItem(position)

                val itemName = view.findViewById<TextView>(R.id.item_name)
                val itemDescription = view.findViewById<TextView>(R.id.item_description)
                val itemDate = view.findViewById<TextView>(R.id.item_date)
                val itemTime = view.findViewById<TextView>(R.id.item_time)
                val deleteButton = view.findViewById<Button>(R.id.delete_appointment_button)
                val editButton = view.findViewById<Button>(R.id.edit_appointment_button)

                itemName.text = appointment?.Username
                itemDescription.text = appointment?.Description
                itemDate.text = appointment?.Date
                itemTime.text = appointment?.Time

                editButton.setOnClickListener {
                    appointment?.let { it1 -> listener?.onEditAppointment(it1) }
                }

                deleteButton.setOnClickListener {
                    appointment?.let { it1 ->
                        listener.deleteAppointment(
                            it1
                        )
                    }
                }

                return view

            }

            AdapterMode.USER -> {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_item_appt_user, parent, false)
                val appointment = getItem(position)

                val itemName = view.findViewById<TextView>(R.id.item_name)
                val itemDescription = view.findViewById<TextView>(R.id.item_description)
                val itemDate = view.findViewById<TextView>(R.id.item_date)
                val itemTime = view.findViewById<TextView>(R.id.item_time)
                val deleteButton = view.findViewById<Button>(R.id.delete_appointment_button)

                itemName.text = appointment?.Date
                itemDescription.text = appointment?.Time
                itemDate.text = appointment?.VetName
                itemTime.text = appointment?.VeterinarianName

                deleteButton.setOnClickListener {
                    appointment?.let { it1 ->
                        listener.deleteAppointment(
                            it1
                        )
                    }
                }

                return view
            }
        }
    }

}

