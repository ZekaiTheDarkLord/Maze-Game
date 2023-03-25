package client

// requires host, port, name
fun main(args: Array<String>) {
    val listeningInterface = RefereeProxy(args[0], args[1].toInt(), args[3])
    listeningInterface.startListening()
}