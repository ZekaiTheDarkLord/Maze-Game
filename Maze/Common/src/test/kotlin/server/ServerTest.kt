package server

import client.RefereeProxy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.TimeoutException
import kotlin.test.assertEquals

class ServerTest {

    val whatClient = Thread {
        val listeningInterface = RefereeProxy("localhost", 5000, "What")
        Thread.sleep(250L)
        listeningInterface.startListening()
    }
    val matthiasClient = Thread {
        val listeningInterface = RefereeProxy("localhost", 5000, "Matthias")
        Thread.sleep(250L)
        listeningInterface.startListening()
    }
    val samClient = Thread {
        val listeningInterface = RefereeProxy("localhost", 5000, "Samasd")
        Thread.sleep(500L)
        listeningInterface.startListening()
    }
    val benClient = Thread {
        val listeningInterface = RefereeProxy("localhost", 5000, "avcasd")
        Thread.sleep(500L)
        listeningInterface.startListening()
    }
    val jamieClient = Thread {
        val listeningInterface = RefereeProxy("localhost", 5000, "Jamie")
        Thread.sleep(250L)
        listeningInterface.startListening()
    }
    val davidClient = Thread {
        val listeningInterface = RefereeProxy("localhost", 5000, "David")
        Thread.sleep(250L)
        listeningInterface.startListening()
    }

    @Test
    fun `server times out after no player join in 40 seconds`() {
        assertThrows<TimeoutException> {
            Server().run(arrayOf("0", "7", "7", "5000"))
        }
    }

    @Test
    fun `server times out after some but not enough player join in 40 seconds`() {
        assertThrows<TimeoutException> {
            Server().run(arrayOf("0", "7", "7", "5000"))
        }
        Thread {
            val listeningInterface = RefereeProxy("localhost", 5000, "zekai")
            listeningInterface.startListening()
        }
    }

    @Test
    fun `game starts immediately after max players reached under 20 seconds`() {
        val serverThread = Thread {
            Server().run(arrayOf("0", "7", "7", "5000"))
        }
        serverThread.start()

        Thread.sleep(1000L)
        whatClient.start()

        Thread.sleep(1000L)
        jamieClient.start()

        Thread.sleep(1000L)
        davidClient.start()

        Thread.sleep(1000L)
        matthiasClient.start()

        Thread.sleep(1000L)
        benClient.start()

        Thread.sleep(1000L)
        samClient.start()


        serverThread.join()

        serverThread.stop()
    }

    @Test
    fun `game starts immediately after max players reached under 40 seconds`() {
        val serverThread = Thread {
            Server().run(arrayOf("0", "7", "7", "5000"))
        }
        serverThread.start()

        Thread.sleep(1000L)
        whatClient.start()

        Thread.sleep(1000L)
        jamieClient.start()

        Thread.sleep(1000L)
        davidClient.start()

        Thread.sleep(1000L)
        matthiasClient.start()

        Thread.sleep(18000L)

        Thread.sleep(1000L)
        benClient.start()

        Thread.sleep(1000L)
        samClient.start()


        serverThread.join()
    }

    @Test
    fun `game starts after min players reached in 20 seconds`() {
        val serverThread = Thread {
            Server().run(arrayOf("0", "7", "7", "5000"))
        }
        serverThread.start()

        Thread.sleep(1000L)
        whatClient.start()

        Thread.sleep(1000L)
        samClient.start()

        serverThread.join()
    }

    @Test
    fun `game starts after min players reached in 40 seconds`() {
        val serverThread = Thread {
            Server().run(arrayOf("0", "7", "7", "5000"))
        }
        serverThread.start()

        Thread.sleep(1000L)
        whatClient.start()

        Thread.sleep(20000L)
        jamieClient.start()


        serverThread.join()

        serverThread.stop()
    }

    @Test
    fun `one player sign ups and another player joins but does not supply name`() {
        assertThrows<TimeoutException> {
            Server().run(arrayOf("0", "7", "7", "5000"))
        }
        Thread.sleep(1000L)
        whatClient.start()

        Thread.sleep(2000L)
        Thread {
            val listeningInterface = RefereeProxy("localhost", 5000, "What")
            //connects but does not send
        }
    }

}