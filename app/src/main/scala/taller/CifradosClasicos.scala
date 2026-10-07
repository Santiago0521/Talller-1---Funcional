package taller

import scala.annotation.tailrec

/**
 * Taller 1 — cifrados clásicos con recursión.
 *
 * Solo se cifran las 26 letras minúsculas del alfabeto inglés; cualquier otro
 * carácter se copia sin cambio.
 */
class CifradosClasicos {

  type Mensaje = String
  type Clave = String

  // Una frecuencia asocia cada letra con las veces que aparece.
  type Frecuencias = List[(Char, Int)]

  val letras = 26
  val primera = 'a'.toInt

  def esMinuscula(c: Char): Boolean = c >= 'a' && c <= 'z'

  // Punto 1 -------------------------------------------------------------------

  /** César con recursión lineal: una operación pendiente por letra. */
  def cesar(m: Mensaje, k: Int): Mensaje = {
    if (m.isEmpty) ""
    else {
      val primero = m.head
      val resto = m.tail

      if (esMinuscula(primero)) {
        val posicion = primero.toInt - primera
        val nuevaPosicion = (posicion + k) % letras
        val ajustada = if (nuevaPosicion < 0) nuevaPosicion + letras 
        else nuevaPosicion
        val nuevaLetra = (primera + ajustada).toChar

        nuevaLetra + cesar(resto, k)
      } else {
        primero + cesar(resto, k)
      }
    }
  }

  // Punto 2 -------------------------------------------------------------------

  /**
   * El mismo César como proceso iterativo: espacio constante.
   * Cuando la función esté escrita, anótela con @tailrec: el compilador
   * comprueba que la llamada recursiva sea lo último que hace.
   */
  @tailrec
  final def cesarCola(m: Mensaje, k: Int, acc: Mensaje = ""): Mensaje = {
    if (m.isEmpty) acc
    else {
      val primero = m.head
      val resto = m.tail

      if (esMinuscula(primero)) {
        val posicion = primero.toInt - primera
        val nuevaPosicion = (posicion + k) % letras
        val ajustada = if (nuevaPosicion < 0) nuevaPosicion + letras 
        else nuevaPosicion
        val nuevaLetra = (primera + ajustada).toChar

        cesarCola(resto, k, acc + nuevaLetra)
      } else {
        cesarCola(resto, k, acc + primero)
      }
    }
  }

  // Punto 3 -------------------------------------------------------------------

  /**
   * Cuenta las letras minúsculas del mensaje, de mayor a menor frecuencia y,
   * en empate, en orden alfabético. El recorrido es recursivo de cola.
   */
  def frecuencias(m: Mensaje): Frecuencias = {
    @tailrec
    def contar(resto: Mensaje, acumuladas: Frecuencias): Frecuencias = {
      if (resto.isEmpty) acumuladas
      else {
        val letra = resto.head

        if (esMinuscula(letra)) {
          val actualizada = acumuladas.map {
            case (c, cantidad) if c == letra => (c, cantidad + 1)
            case par => par
          }
          if (actualizada.exists(_._1 == letra)) {
            contar(resto.tail, actualizada)
          } else {
            contar(resto.tail, (letra, 1) :: acumuladas)
          }
        } else {
          contar(resto.tail, acumuladas)
        } 
      }
    }
    contar(m, List()).sortBy { 
      case (letra, cantidad) => (-cantidad, letra) 
    }
  }

  // Punto 4 -------------------------------------------------------------------

  /**
   * Supone que la letra más frecuente del mensaje cifrado es la 'e' del
   * original y devuelve la distancia entre las dos. Sin letras, cero.
   */
  def desplazamientoProbable(m: Mensaje): Int = {
    val frecs = frecuencias(m)
    
    if (frecs.isEmpty) 0
    else {
      val letraMasFrecuente = frecs.head._1 
      (letraMasFrecuente.toInt - 'e'.toInt + letras) % letras
    }
  }

  def romperCesar(m: Mensaje): Mensaje = {
    cesar(m, -desplazamientoProbable(m))
  }

  // Punto 5 -------------------------------------------------------------------

  /**
   * Cuántos mensajes de longitud n se forman con a letras sin dos iguales
   * seguidas.
   */
  def combinaciones(n: Int, a: Int): BigInt = {
    if (n == 0) BigInt(1)
    else {
      if (n == 1) BigInt(a)
      else {
        BigInt(a - 1) * combinaciones(n - 1, a) 
      }
    } 
  }

  /**
   * Vigenère: cada letra se corre según la letra de la clave que le toca. Lo
   * que no es letra minúscula se copia y no consume clave.
   */
  def vigenere(m: Mensaje, clave: Clave): Mensaje = {
    if (clave.isEmpty) m
    else {
      @tailrec
      def cifrar(resto: Mensaje, posicionClave: Int, acc: Mensaje): Mensaje = {
        if (resto.isEmpty) acc
        else {
          val primero = resto.head

          if (esMinuscula(primero)) {
            val letraClave = clave.charAt(posicionClave)
            val desplazamiento = letraClave.toInt - primera

            val posicion = primero.toInt - primera
            val nuevaPosicion = (posicion + desplazamiento) % letras
            val nuevaLetra = (primera + nuevaPosicion).toChar

            val siguientePosicion = (posicionClave + 1) % clave.length
            cifrar(resto.tail, siguientePosicion, acc + nuevaLetra)
          } else {
            cifrar(resto.tail, posicionClave, acc + primero)
          }
        }
      }
      cifrar(m, 0, "")
    }
  }
}