import com.google.gson.Gson
import state.IState
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.jvm.Throws

// Utility functions

/**
 * Convert a given game state to its JSON representation in String
 * @param gameState the gameState to convert to JSON string
 * @return the string JSON representation of the given gameState
 */
fun convertGameStateToJSON(gameState: IState): String {
    return Gson().toJson(gameState)
}


/**
 * Gets an bufferedImage of a particular gem.
 * @param file the gem file
 * @throws IOException an error occurs when reading
 */
@Throws(IOException::class)
fun getGemImage(file: File): BufferedImage {
    return ImageIO.read(file)
}

@Throws(IOException::class)
fun createFile(pathAndName: String, content: String) {
    val file = File(pathAndName)
    file.writeText(content)
}
