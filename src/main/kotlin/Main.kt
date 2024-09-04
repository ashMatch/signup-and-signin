package org.example

import org.example.Client.Client
import org.mindrot.jbcrypt.BCrypt



fun mainMenu(clients:MutableList<Client>):Int{
    var option:Int
    println("Olá seja bem vindo ao mundo virtual\n")
    println("""
        1.Já tenho uma conta (SignIn)
        2.Ainda não tenho uma conta (SignUp)
        3. Entrar como visitante (Login)
    """.trimMargin())

    option = readln()!!.toInt()
    if(option == 1){
        println("")
    }
    else if(option == 2){
        signUpSystem(clients)
    }
//    println()
    return option
}

fun signUpSystem(clients:MutableList<Client>){
    println("Digite seu nome: ")
    var name:String = readln()
    println("Digite um email: ")
    var email:String = readln()
    println("Digite uma senha: ")
    var password:String = readln()
    if(isValidPassword(password)){

        //Atualiza a variavel password com o hash retornado
        password = addHashEncryptBCryp(password)
        var client = Client(name, email, password)
        println("Senha válida")
        clients.add(client)
        clients.forEach{it -> println("""
            nome: ${it.name}:
            email: ${it.email}:
            senha: ${it.password}
        """.trimIndent())}


    } else {
        println("Senha inválida")
        signUpSystem(clients)

    }
}

fun addHashEncryptBCryp(password: String):String {
    // implementação simples na doc https://www.javadoc.io/doc/org.mindrot/jbcrypt/0.4/org/mindrot/jbcrypt/BCrypt.html
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun isValidPassword(password:String): Boolean{
    //Verify if there is a space
    if (password.contains(" ")){
        println("Erro: A senha não pode conter espaços.")
        return false
    }

    // Expressões regulares para validação da senha
    val regexUpperCase = Regex(".*[A-Z].*") // Pelo menos uma letra maiúscula
    val regexDigit = Regex(".*\\d.*") // Pelo menos um digito
    val regexSymbol = Regex(".*[^A-Za-z0-9].*") // Pelo menos um símbolo

    //Verificação usando as regex
    val hasRegexUpperCase = regexUpperCase.containsMatchIn(password)
    val hasRegexDigit = regexDigit.containsMatchIn(password)
    val hasRegexSymbol = regexSymbol.containsMatchIn(password)

    //Checa se todas as condições estão satisfeitas
    if (hasRegexUpperCase && hasRegexDigit && hasRegexSymbol){
        return true
    }

    if(!hasRegexUpperCase) println("Erro: senha deve conter pelo menos uma letra maiúscula.")
    if(!hasRegexDigit) println("Erro: senha deve conter pelo menos um numero.")
    if(!hasRegexSymbol) println("Erro: senha deve conter pelo menos um simbolo.")

    return false

}

fun main() {
    val clients:MutableList<Client> = mutableListOf()
    mainMenu(clients)
}