package top.itloser.piglin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import top.itloser.piglin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            notifyTest()

        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        createChannel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private val channelId = "device_alarms"
    private val channelName = "设备报警通知"
    private fun createChannel() {
        val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels: MutableList<NotificationChannel> = ArrayList()
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(true) //启用震动
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000) //设置震动模式
            channel.enableLights(true) //启用呼吸灯
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC //锁屏显示
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) channel.setAllowBubbles(true) //强提醒
            channels.add(channel)
            manager.createNotificationChannels(channels)
        }
    }


    private fun notifyTest() {

        val title = "华为通知栏测试"
        val content = "华为通知不显示 但是有震动"

        //设置点击事件 进入事件列表页面
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("deviceId", "YTH20008AD36")
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 8.0 以上 使用通知渠道 app安装后无权设置 让用户自行设置
            builder = Notification.Builder(this, channelId)

        } else {

            // 8.0 以下 构造 震动 和 声音
            builder = Notification.Builder(this)
            builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE)
            //振动
            val vibrates = longArrayOf(1000, 500, 1000)
            builder.setVibrate(vibrates)

            //声音
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE)
            builder.setSound(uri)
        }

        //具体通知的内容

        //具体通知的内容
        builder.setSmallIcon(R.drawable.ic_piglin)
            .setContentTitle(title) //标题
            .setContentText(content) //内容
            .setAutoCancel(true) //点击后 自动消失
            .setContentIntent(pi) //点击事件

        val notification = builder.build()

        //发送通知
        val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notifyId++, notification)

    }

    var notifyId = 0


}