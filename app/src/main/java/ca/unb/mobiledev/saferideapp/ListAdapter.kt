package ca.unb.mobiledev.saferideapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.saferideapp.sampledata.WaitList
import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.Toast
import ca.unb.mobiledev.saferideapp.utils.DatabaseHelper

class ListAdapter(private val parentActivity: Activity, private val mDataset: ArrayList<WaitList>, private val locationPickUp: String) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val riderNameTextView: TextView = itemView.findViewById(R.id.textName)
        val buttonAction: Button = itemView.findViewById(R.id.btnRemove_rider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rider = mDataset[position]
        val dbHelper = DatabaseHelper(parentActivity, locationPickUp)
        holder.riderNameTextView.text = rider.nameItem

        holder.riderNameTextView.setOnClickListener {
            val intent = Intent(parentActivity, DetailActivity::class.java)
            intent.putExtra("id", rider.idItem)
            intent.putExtra("name", rider.nameItem)
            intent.putExtra("dropOffLocation", rider.location)
            parentActivity.startActivity(intent)
        }
        holder.buttonAction.setOnClickListener {
            showConfirmationDialog(dbHelper, position)

        }
    }
    private fun showConfirmationDialog(dbHelper: DatabaseHelper, position: Int ){
        val alertDialogBuilder = AlertDialog.Builder(parentActivity)

        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to proceed?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val deleted = dbHelper.removeRider(mDataset[position].idItem, locationPickUp)


            if (deleted > 0) {
                mDataset.removeAt(position)
                notifyDataSetChanged()

                Toast.makeText(parentActivity, "Rider removed", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(parentActivity, "Failed to remove rider", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    override fun getItemCount(): Int {
        return mDataset.size
    }
    companion object {
        private const val TAG = "My Adaptor"
    }
}
