# Corrección

## Introducción

En este informe argumentaremos la corrección de las funciones implementadas en el taller de Cifrados Clásicos. El objetivo es justificar que cada función cumple con el comportamiento establecido en el enunciado.

Las funciones trabajan únicamente con las **26 letras minúsculas del alfabeto inglés** cuando se trata de cifrado o conteo de frecuencias. Los caracteres que no son letras minúsculas se conservan sin modificación en los cifrados y no se cuentan en `frecuencias`.

Las demostraciones se realizan mediante inducción sobre la estructura del mensaje o sobre el tamaño del problema cuando la función es recursiva. Para las funciones de recursión de cola se utiliza un **invariante** que describe qué representa el acumulador en cada paso.

---

# 1. Corrección de `cesar`

La función `cesar` recibe un mensaje `m` y un desplazamiento `k`. Para cada letra minúscula calcula su nueva posición en el alfabeto mediante:

$$
p' = (p + k) \bmod 26
$$

donde \(p\) es la posición de la letra original y \(p'\) es la posición de la letra cifrada.

Los caracteres que no son letras minúsculas se copian sin cambios.

### Caso base

Si el mensaje es vacío:

```scala
cesar("", k)
```

la función devuelve:

```text
""
```

Esto es correcto porque no existen caracteres que cifrar.

### Paso inductivo

Supongamos que `cesar` funciona correctamente para el resto del mensaje `resto`.

Para un mensaje:

```text
primero + resto
```

la función analiza `primero`.

Si `primero` es una letra minúscula, calcula:

$$
p = \text{posición}(primero)
$$

y después:

$$
p' = (p+k)\bmod 26
$$

La letra correspondiente a \(p'\) es la letra cifrada.

Luego aplica recursivamente:

```scala
cesar(resto, k)
```

Por hipótesis inductiva, esta llamada cifra correctamente el resto del mensaje. Finalmente, la función concatena la letra cifrada con el resultado obtenido para el resto.

Si `primero` no es una letra minúscula, se conserva:

```scala
primero + cesar(resto, k)
```

Por lo tanto, el primer carácter se procesa correctamente y el resto también.

### Corrección

Por inducción sobre la longitud del mensaje, `cesar(m, k)` devuelve exactamente el mensaje en el que cada letra minúscula ha sido desplazada `k` posiciones y todos los demás caracteres permanecen sin cambios.

---

# 2. Corrección de `cesarCola`

`cesarCola` realiza el mismo cifrado que `cesar`, pero utilizando un acumulador:

```scala
cesarCola(m, k, acc)
```

El acumulador contiene el resultado que ya ha sido procesado.

## Invariante

Durante toda la ejecución se mantiene el siguiente invariante:

> `acc` contiene exactamente el cifrado de los caracteres que ya fueron procesados del mensaje original.

Por tanto, si el mensaje original puede dividirse como:

$$
m = procesado + resto
$$

el acumulador representa:

$$
acc = cesar(procesado,k)
$$

### Estado inicial

La llamada comienza normalmente como:

```scala
cesarCola(m, k, "")
```

El acumulador está vacío porque todavía no se ha procesado ningún carácter.

El invariante se cumple.

### Transformación

En cada llamada se toma:

```scala
val primero = m.head
```

Si es una letra minúscula, se calcula su desplazamiento y se agrega la nueva letra:

```scala
cesarCola(resto, k, acc + nuevaLetra)
```

Si no es una letra minúscula, se conserva:

```scala
cesarCola(resto, k, acc + primero)
```

En ambos casos, el nuevo acumulador contiene el resultado correcto para todos los caracteres procesados hasta ese momento. Por lo tanto, el invariante se conserva.

### Caso final

Cuando:

```scala
m.isEmpty
```

la función devuelve:

```scala
acc
```

En ese momento ya no quedan caracteres por procesar. Por el invariante, `acc` contiene el cifrado completo del mensaje original.

Por tanto:

$$
cesarCola(m,k,"") = cesar(m,k)
$$

La función es correcta.

Además, la llamada recursiva es la última operación realizada, por lo que está correctamente anotada con `@tailrec`.

---

# 3. Corrección de `frecuencias`

La función `frecuencias` calcula cuántas veces aparece cada letra minúscula del mensaje.

Los caracteres que no son letras minúsculas son ignorados.

Después del recorrido, ordena los resultados mediante:

```scala
sortBy {
  case (letra, cantidad) => (-cantidad, letra)
}
```

Esto produce primero las letras con mayor frecuencia y, en caso de empate, las ordena alfabéticamente.

## Invariante del recorrido

La función auxiliar:

```scala
contar(resto, acumuladas)
```

mantiene el siguiente invariante:

> `acumuladas` contiene exactamente las frecuencias de todas las letras minúsculas que ya fueron procesadas.

### Estado inicial

La ejecución comienza con:

```scala
contar(m, List())
```

Todavía no se ha procesado ningún carácter, por lo que la lista de frecuencias está vacía.

El invariante se cumple.

### Procesamiento de una letra

Si el primer carácter es una letra minúscula, se busca dentro de `acumuladas`.

Si ya existe, su cantidad se incrementa en uno:

```scala
(c, cantidad + 1)
```

Si todavía no existe, se añade:

```scala
(letra, 1) :: acumuladas
```

Por lo tanto, la frecuencia de la letra procesada queda actualizada correctamente.

### Procesamiento de un carácter no válido

Si el carácter no es una letra minúscula, se realiza:

```scala
contar(resto.tail, acumuladas)
```

El acumulador no cambia porque ese carácter no debe formar parte de las frecuencias.

### Caso final

Cuando `resto` está vacío:

```scala
if (resto.isEmpty) acumuladas
```

ya se han procesado todos los caracteres.

Por el invariante, `acumuladas` contiene las frecuencias correctas de todas las letras minúsculas del mensaje.

Finalmente se ordena la lista por:

$$
(-cantidad, letra)
$$

El signo negativo hace que las mayores cantidades aparezcan primero y `letra` resuelve los empates en orden alfabético.

Por tanto, `frecuencias` devuelve exactamente las frecuencias solicitadas.

---

# 4. Corrección de `desplazamientoProbable`

La función `desplazamientoProbable` parte de la hipótesis indicada por el enunciado:

> La letra más frecuente del mensaje cifrado corresponde a la letra `e` del mensaje original.

Primero obtiene:

```scala
val frecs = frecuencias(m)
```

Como `frecuencias` devuelve las letras ordenadas de mayor a menor frecuencia, si la lista no está vacía:

```scala
frecs.head._1
```

es la letra más frecuente.

Sea \(c\) la posición de esa letra y sea \(e\) la posición de la letra `e`.

El desplazamiento se calcula como:

$$
k = (c-e+26)\bmod 26
$$

La suma de `26` permite que el resultado sea válido incluso cuando `c` se encuentra antes de `e` en el alfabeto.

Por ejemplo, si la letra más frecuente es `z`:

$$
k = (25-4+26)\bmod26
$$

$$
k = 47\bmod26 = 21
$$

Por lo tanto, la función devuelve correctamente el desplazamiento probable.

Si no existen letras:

```scala
if (frecs.isEmpty) 0
```

devuelve `0`, que representa que no puede determinarse ningún desplazamiento y se utiliza el valor indicado por la implementación.

---

# 5. Corrección de `romperCesar`

La función:

```scala
def romperCesar(m: Mensaje): Mensaje = {
  cesar(m, -desplazamientoProbable(m))
}
```

utiliza el desplazamiento probable obtenido a partir de las frecuencias.

Supongamos que el mensaje original fue cifrado utilizando un desplazamiento \(k\).

El cifrado César realiza:

$$
p' = (p+k)\bmod26
$$

Para recuperar la posición original se aplica el desplazamiento contrario:

$$
p = (p'-k)\bmod26
$$

La función obtiene el desplazamiento probable `k` y aplica:

```scala
cesar(m, -k)
```

Por la corrección de `cesar`, esto desplaza cada letra en la dirección contraria.

Por tanto, bajo la hipótesis de que la letra más frecuente del mensaje cifrado corresponde a `e`, `romperCesar` recupera el mensaje original.

Si el mensaje no contiene letras minúsculas, `desplazamientoProbable` devuelve `0`, por lo que el mensaje permanece sin cambios.

---

# 6. Corrección de `combinaciones`

La función `combinaciones(n, a)` calcula cuántos mensajes de longitud `n` pueden formarse utilizando `a` letras sin que haya dos letras iguales consecutivas.

La recurrencia implementada es:

$$
C(0,a)=1
$$

$$
C(1,a)=a
$$

y para 

$$
\(n\geq2\):
$$

$$
C(n,a)=(a-1)C(n-1,a)
$$

## Caso base: \(n=0\)

La función devuelve:

```scala
BigInt(1)
```

Existe exactamente una cadena de longitud cero: la cadena vacía.

Por tanto:

$$
C(0,a)=1
$$

es correcto.

## Caso base: \(n=1\)

La función devuelve:

```scala
BigInt(a)
```

Para una cadena de una sola posición, cualquiera de las `a` letras disponibles puede utilizarse.

Por tanto:

$$
C(1,a)=a
$$

es correcto.

## Paso inductivo

Supongamos que:

$$
C(n-1,a)
$$

representa correctamente el número de mensajes de longitud \(n-1\) sin letras iguales consecutivas.

Para construir un mensaje de longitud \(n\), tomamos uno de esos mensajes y añadimos una letra al final.

La nueva letra no puede ser igual a la última letra utilizada. Si existen `a` letras disponibles, quedan:

$$
a-1
$$

opciones válidas.

Por tanto:

$$
C(n,a)=(a-1)C(n-1,a)
$$

que es exactamente la operación implementada:

```scala
BigInt(a - 1) * combinaciones(n - 1, a)
```

Por inducción sobre `n`, `combinaciones` calcula correctamente el número de mensajes solicitado.

---

# 7. Corrección de `vigenere`

La función `vigenere` cifra un mensaje utilizando una clave. Cada letra minúscula del mensaje se desplaza según la letra correspondiente de la clave.

Si la letra del mensaje tiene posición \(p\) y la letra de la clave tiene posición \(k\), la nueva posición es:

$$
p'=(p+k)\bmod26
$$

La clave se recorre de forma circular mediante:

```scala
(posicionClave + 1) % clave.length
```

## Caso de clave vacía

Si:

```scala
clave.isEmpty
```

la función devuelve directamente:

```scala
m
```

Esto evita intentar acceder a una posición inexistente de la clave.

## Invariante

La función auxiliar:

```scala
cifrar(resto, posicionClave, acc)
```

mantiene el siguiente invariante:

> `acc` contiene exactamente el resultado del cifrado de los caracteres minúsculos procesados hasta el momento, y `posicionClave` indica la posición de la clave que debe utilizarse para la siguiente letra minúscula.

### Procesamiento de una letra minúscula

Si `primero` es una letra minúscula, se obtiene:

```scala
val letraClave = clave.charAt(posicionClave)
```

La posición de la letra de la clave determina el desplazamiento:

$$
desplazamiento = posición(letraClave)
$$

Después se calcula:

$$
nuevaPosicion =
(posición(primero)+desplazamiento)\bmod26
$$

y se agrega la nueva letra al acumulador.

Finalmente, la posición de la clave avanza:

```scala
val siguientePosicion =
  (posicionClave + 1) % clave.length
```

Por lo tanto, el invariante se mantiene.

### Procesamiento de caracteres que no son letras minúsculas

Si el carácter no es una letra minúscula, se realiza:

```scala
cifrar(resto.tail, posicionClave, acc + primero)
```

El carácter se copia sin modificar y, además, `posicionClave` no cambia.

Esto es correcto porque los caracteres que no son letras minúsculas no deben consumir posiciones de la clave.

### Caso final

Cuando `resto` está vacío:

```scala
if (resto.isEmpty) acc
```

todos los caracteres han sido procesados.

Por el invariante, `acc` contiene el mensaje cifrado completo y `posicionClave` ya no necesita avanzar.

Por tanto, `vigenere` produce exactamente el cifrado Vigenère especificado.

---

# 8. Relación entre las funciones

Las funciones también pueden considerarse correctas en conjunto.

El proceso para romper un cifrado César es:

$$
m
\rightarrow frecuencias(m)
\rightarrow desplazamientoProbable(m)
\rightarrow cesar(m,-k)
$$

`frecuencias` identifica la letra que aparece más veces, `desplazamientoProbable` estima el desplazamiento suponiendo que dicha letra corresponde a `e`, y `romperCesar` aplica el desplazamiento contrario.

Por otro lado, `cesar` y `cesarCola` realizan la misma transformación:

$$
cesar(m,k)=cesarCola(m,k,"")
$$

La diferencia entre ambas funciones no está en el resultado, sino en la forma de realizar la recursión. `cesar` conserva una operación pendiente después de la llamada recursiva, mientras que `cesarCola` utiliza un acumulador y realiza la llamada recursiva como última operación.

---

# Conclusión

Las funciones implementadas cumplen las especificaciones del taller bajo las condiciones establecidas:

* `cesar` desplaza las letras minúsculas según un desplazamiento dado y conserva los demás caracteres.
* `cesarCola` realiza el mismo cifrado utilizando recursión de cola y un acumulador.
* `frecuencias` cuenta las letras minúsculas y las ordena por frecuencia descendente y orden alfabético en caso de empate.
* `desplazamientoProbable` estima el desplazamiento suponiendo que la letra más frecuente representa a `e`.
* `romperCesar` aplica el desplazamiento contrario para intentar recuperar el mensaje original.
* `combinaciones` calcula el número de mensajes de longitud `n` sin letras iguales consecutivas mediante la recurrencia correspondiente.
* `vigenere` aplica los desplazamientos definidos por una clave circular y no consume clave al encontrar caracteres que no sean letras minúsculas.

Las demostraciones mediante casos base, pasos inductivos e invariantes permiten justificar que los resultados obtenidos por las funciones corresponden con las operaciones definidas en el enunciado.
