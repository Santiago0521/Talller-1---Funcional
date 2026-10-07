package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

/**
 * Cada ejemplo del enunciado es una prueba. Si el enunciado promete un valor,
 * aquí se comprueba que la solución lo produce.
 */
@RunWith(classOf[JUnitRunner])
class CifradosClasicosTest extends AnyFunSuite {

  val c = new CifradosClasicos()
  import c._

  // Punto 1: ejemplos del enunciado -------------------------------------------

  test("cesar: casa con 3 da fdvd") { assert(cesar("casa", 3) == "fdvd") }
  test("cesar: fdvd con -3 vuelve a casa") { assert(cesar("fdvd", -3) == "casa") }
  test("cesar: hola mundo con 1") { assert(cesar("hola mundo", 1) == "ipmb nvoep") }
  test("cesar: zzz con 1 da aaa") { assert(cesar("zzz", 1) == "aaa") }
  test("cesar: 29 es lo mismo que 3") { assert(cesar("abc", 29) == "def") }
  test("cesar: el mensaje vacío sale vacío") { assert(cesar("", 5) == "") }

  test("cesar: la puntuación y los dígitos pasan sin cambio") {
    assert(cesar("ab, 12!", 1) == "bc, 12!")
  }

  test("cesar: las mayúsculas no se cifran") {
    assert(cesar("Casa", 3) == "Cdvd")
  }

  test("cesar: cifrar y descifrar es la identidad") {
    assert(cesar(cesar("un mensaje cualquiera", 11), -11) == "un mensaje cualquiera")
  }

  // Punto 2 -------------------------------------------------------------------

  test("cesarCola: casa con 3 da fdvd") { assert(cesarCola("casa", 3) == "fdvd") }
  test("cesarCola: hola mundo con 1") { assert(cesarCola("hola mundo", 1) == "ipmb nvoep") }
  test("cesarCola: con 0 el mensaje no cambia") { assert(cesarCola("abc", 0) == "abc") }

  test("cesarCola: da lo mismo que la versión lineal") {
    val casos = List(("casa", 3), ("hola mundo", 1), ("zzz", 1), ("abc", 29),
                     ("", 5), ("ab, 12!", -4))
    assert(casos.forall { case (m, k) => cesarCola(m, k) == cesar(m, k) })
  }

  test("cesarCola: aguanta un mensaje largo sin desbordar la pila") {
    val largo = "abcdefghij" * 20000
    assert(cesarCola(largo, 1).length == largo.length)
  }

  // Punto 3 -------------------------------------------------------------------

  test("frecuencias: casa") {
    assert(frecuencias("casa") == List(('a', 2), ('c', 1), ('s', 1)))
  }

  test("frecuencias: aabbbc") {
    assert(frecuencias("aabbbc") == List(('b', 3), ('a', 2), ('c', 1)))
  }

  test("frecuencias: hola mundo") {
    assert(frecuencias("hola mundo") ==
      List(('o', 2), ('a', 1), ('d', 1), ('h', 1), ('l', 1), ('m', 1),
           ('n', 1), ('u', 1)))
  }

  test("frecuencias: el mensaje vacío no tiene letras") {
    assert(frecuencias("") == List())
  }

  test("frecuencias: un mensaje sin letras no tiene frecuencias") {
    assert(frecuencias("123 !?") == List())
  }

  test("frecuencias: en empate manda el orden alfabético") {
    assert(frecuencias("ba") == List(('a', 1), ('b', 1)))
  }

  // Punto 4 -------------------------------------------------------------------

  test("desplazamientoProbable: h está 3 después de e") {
    assert(desplazamientoProbable("h") == 3)
  }

  test("desplazamientoProbable: hhhaa, con h como la más frecuente") {
    assert(desplazamientoProbable("hhhaa") == 3)
  }

  test("desplazamientoProbable: sin letras da 0") {
    assert(desplazamientoProbable("123") == 0)
  }

  test("desplazamientoProbable: en empate manda la primera alfabéticamente") {
    // 'a' y 'h' aparecen tres veces; gana 'a', que está 22 después de 'e'.
    assert(desplazamientoProbable("hhhaaa") == 22)
  }

  test("romperCesar: recupera un mensaje con suficientes letras e") {
    val original = "el mensaje secreto"
    assert(romperCesar(cesar(original, 7)) == original)
  }

  test("romperCesar: el método falla cuando la e no es la más frecuente") {
    // En este mensaje la letra más frecuente es la 'a', no la 'e'.
    val original = "cada casa amarilla"
    assert(romperCesar(cesar(original, 7)) != original)
  }

  // Punto 5 -------------------------------------------------------------------

  test("combinaciones: con longitud 0 hay un mensaje, el vacío") {
    assert(combinaciones(0, 26) == BigInt(1))
  }

  test("combinaciones: con longitud 1 hay tantos como letras") {
    assert(combinaciones(1, 26) == BigInt(26))
  }

  test("combinaciones: 3 letras sobre 26 dan 16250") {
    assert(combinaciones(3, 26) == BigInt(16250))
  }

  test("combinaciones: 2 letras sobre un alfabeto de 2 dan 2") {
    assert(combinaciones(2, 2) == BigInt(2))
  }

  test("combinaciones: crece según la recurrencia") {
    assert(combinaciones(5, 4) == BigInt(3) * combinaciones(4, 4))
  }

  test("vigenere: ataque con la clave sol") {
    assert(vigenere("ataque", "sol") == "shliip")
  }

  test("vigenere: hola mundo con la clave ab") {
    assert(vigenere("hola mundo", "ab") == "hplb mvneo")
  }

  test("vigenere: con la clave vacía el mensaje no cambia") {
    assert(vigenere("casa", "") == "casa")
  }

  test("vigenere: el espacio no consume letra de la clave") {
    // Sin el espacio la clave iría corrida y la m se cifraría con b.
    assert(vigenere("hola mundo", "ab").charAt(5) == 'm')
  }

  test("vigenere: con una clave de una sola letra es un César") {
    assert(vigenere("hola mundo", "d") == cesar("hola mundo", 3))
  }

  // Nuevos tests --------------------------------------------------------------
  // Punto 1: cesar ------------------------------------------------------------

  test("cesar: desplaza correctamente una letra intermedia") {
    assert(cesar("m", 5) == "r")
  }

  test("cesar: un desplazamiento de 26 deja el mensaje igual") {
    assert(cesar("abcdefghijklmnopqrstuvwxyz", 26) ==
      "abcdefghijklmnopqrstuvwxyz")
  }

  test("cesar: un desplazamiento negativo cruza el inicio del alfabeto") {
    assert(cesar("abc", -1) == "zab")
  }

  test("cesar: un desplazamiento mayor que 26 se reduce correctamente") {
    assert(cesar("xyz", 52) == "xyz")
  }

  test("cesar: conserva todos los caracteres que no son minúsculas") {
    assert(cesar("Hola, Mundo! 123.", 5) == "Htqf, Mzsit! 123.")
  }

  // Punto 2: cesarCola --------------------------------------------------------

  test("cesarCola: un desplazamiento negativo cruza el inicio del alfabeto") {
    assert(cesarCola("abc", -1) == "zab")
  }

  test("cesarCola: conserva caracteres que no son letras minúsculas") {
    assert(cesarCola("Hola, 123!", 4) == "Hspe, 123!")
  }

  test("cesarCola: un desplazamiento de 26 no cambia el mensaje") {
    assert(cesarCola("abcdefghijklmnopqrstuvwxyz", 26) ==
      "abcdefghijklmnopqrstuvwxyz")
  }

  test("cesarCola: funciona con un mensaje de una sola letra") {
    assert(cesarCola("z", 1) == "a")
  }

  test("cesarCola: funciona con un mensaje compuesto solo por caracteres no cifrables") {
    assert(cesarCola("123 !?,.", 10) == "123 !?,.")
  }

  // Punto 3: frecuencias ------------------------------------------------------

  test("frecuencias: ignora mayúsculas") {
    assert(frecuencias("AaBbAa") == List(('a', 2), ('b', 1)))
  }

  test("frecuencias: ignora números y signos") {
    assert(frecuencias("a1!a?b2b.") ==
      List(('a', 2), ('b', 2)))
  }

  test("frecuencias: una sola letra aparece una vez") {
    assert(frecuencias("x") == List(('x', 1)))
  }

  test("frecuencias: ordena primero por frecuencia y luego alfabéticamente") {
    assert(frecuencias("ccaaabbbdd") ==
      List(('a', 3), ('b', 3), ('c', 2), ('d', 2)))
  }

  test("frecuencias: caracteres repetidos separados por otros caracteres se acumulan") {
    assert(frecuencias("a-b-a_c") ==
      List(('a', 2), ('b', 1), ('c', 1)))
  }

  // Punto 4: desplazamientoProbable y romperCesar ---------------------------

  test("desplazamientoProbable: z como letra más frecuente da 21") {
    assert(desplazamientoProbable("zzzz") == 21)
  }

  test("desplazamientoProbable: e como letra más frecuente da 0") {
    assert(desplazamientoProbable("eeeee") == 0)
  }

  test("desplazamientoProbable: ignora caracteres que no son minúsculas") {
    assert(desplazamientoProbable("!!!111hhh???") == 3)
  }

  test("romperCesar: recupera un mensaje cifrado con desplazamiento negativo") {
    val original = "este mensaje tiene muchas letras e"
    assert(romperCesar(cesar(original, -4)) == original)
  }

  test("romperCesar: un mensaje vacío permanece vacío") {
    assert(romperCesar("") == "")
  }

  // Punto 5: combinaciones y vigenere ----------------------------------------

  test("combinaciones: con longitud 0 el resultado siempre es 1") {
    assert(combinaciones(0, 100) == BigInt(1))
  }

  test("combinaciones: con longitud 2 y alfabeto de 3 da 6") {
    assert(combinaciones(2, 3) == BigInt(6))
  }

  test("combinaciones: con longitud 4 y alfabeto de 2 da 2") {
    assert(combinaciones(4, 2) == BigInt(2))
  }

  test("vigenere: una clave de z desplaza cada letra 25 posiciones") {
    assert(vigenere("abc", "z") == "zab")
  }

  test("vigenere: conserva mayúsculas, números y signos sin consumir la clave") {
    assert(vigenere("A1a!b", "bc") == "A1b!d")
  }
}
