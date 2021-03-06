package com.example.glossaryapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.glossaryapp.R
import com.example.glossaryapp.adapters.AdapterOrdersPlaced
import com.example.glossaryapp.app.Endpoints
import com.example.glossaryapp.database.DBHelper
import com.example.glossaryapp.helpers.SessionManager
import com.example.glossaryapp.models.Data
import com.example.glossaryapp.models.OrdersResults
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_orders.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.layout_menu_cart.view.*

class OrdersActivity : AppCompatActivity() {
    var textViewShoppingCartCount: TextView? = null
    lateinit var dbHelper: DBHelper
    lateinit var sessionManager: SessionManager
    var myList: ArrayList<Data> = ArrayList()
    var adapterOrdersPlaced: AdapterOrdersPlaced? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        dbHelper = DBHelper(this)
        sessionManager = SessionManager(this)
        init()
    }

    private fun init() {
        setupToolbar()
        getData()
        adapterOrdersPlaced = AdapterOrdersPlaced(this, myList)
        recycler_view_orders.layoutManager = LinearLayoutManager(this)
        recycler_view_orders.adapter = adapterOrdersPlaced
    }

    private fun getData() {
        var userId = sessionManager.getUserId()
        var request = StringRequest(Request.Method.GET, Endpoints.getOrdersByUserId(userId), {
            var gson = Gson()
            var ordersResult = gson.fromJson(it, OrdersResults::class.java)
            myList.addAll(ordersResult.data)
            adapterOrdersPlaced?.setData(myList)
        },
            {

            }
        )
        Volley.newRequestQueue(this).add(request)
    }


    private fun setupToolbar() {
        var toolbar = toolbar
        toolbar.title = "Your Cart"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cart, menu)
        var item = menu.findItem(R.id.action_cart)
        MenuItemCompat.setActionView(item, R.layout.layout_menu_cart)
        var view = MenuItemCompat.getActionView(item)
        textViewShoppingCartCount = view.text_view_cart_count
        view.setOnClickListener {
            startActivity(Intent(applicationContext, ShoppingCartActivity::class.java))
        }
        updateShoppingCartCount()
        return true
    }

    private fun updateShoppingCartCount() {
        var myCount = dbHelper.getCartTotalCount()
        if(myCount == 0) {
            textViewShoppingCartCount?.visibility = View.INVISIBLE
        } else {
            textViewShoppingCartCount?.visibility = View.VISIBLE
            textViewShoppingCartCount?.text = myCount.toString()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}