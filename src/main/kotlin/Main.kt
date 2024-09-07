package org.example

import org.example.Client.Client
import org.mindrot.jbcrypt.BCrypt
import org.xbill.DNS.*
import org.slf4j.LoggerFactory
import org.xbill.DNS.Message
import java.util.Properties
import javax.mail.*
import javax.mail.Message.RecipientType
import javax.mail.internet.*


val logger =  LoggerFactory.getLogger("DnsEmailChecker")
var recipient = ""
val subject = "Confirmação do Registro"
val body = "Olá, este é um e-mail de confirmação. Por favor, confirme seu e-mail."


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
    while (!isValidEmail(email)){
        println("Email inválido, informe um email válido")
        println("Digite um email: ")
        email = readln()
    }
    if (isValidEmail(email)){
        print("Email valido!\nchecando se ele existe...")
        dnsEmailChecking(email)
    }
    println("Digite uma senha: ")
    var password:String = readln()
    if(isValidPassword(password)){

        //Atualiza a variavel password com o hash retornado
        password = addHashEncryptBCrypt(password)
        val client = Client(name, email, password)
        println("Senha válida")
        sendValidationEmail(email, subject, body)
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

// implementação simples na doc https://www.javadoc.io/doc/org.mindrot/jbcrypt/0.4/org/mindrot/jbcrypt/BCrypt.html
fun addHashEncryptBCrypt(password: String):String = BCrypt.hashpw(password, BCrypt.gensalt())

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

fun isValidEmail(email:String): Boolean{
    val regexEmail = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")

    return email.matches(regexEmail)
}

fun dnsEmailChecking(email:String){
    try {
        // Extrair o domínio do e-mail
        val domain = email.substringAfter('@')

        val lookup = Lookup(domain, Type.MX)

        // Faz a query e joga em uma variavel
        val records = lookup.run()

        if(lookup.result == Lookup.SUCCESSFUL){

            //Verifica se há registros MX
            if(records != null && records.isNotEmpty()){
                //Itera sobre os registros e valida
                for (record in records){
                    val mxRecord = record as MXRecord
                    println("MX Record: Host=${mxRecord.target}, Prioridade=${mxRecord.priority}")

                    //Exemplo simples de verificação: Você pode validar o domínio do MX Record
                    if(isMXRecordValid(mxRecord.target.toString())){
                        println("MX Record válido encontrado!")
                    } else {
                        println("MX Record inválido ou suspeito: ${mxRecord.target}")
                    }

                }

            } else{
                println("Nenhum registro MX encontrado para o dominio")

            }

        }
        else {
            println("Erro: Deu ruim irmao")

        }
    } catch (e: Exception){
        logger.error("Erro ao fazer a consulta DNS ", e)
    }

}

// função simples para a validação dos resgistros MX (modifique conforme necessidade)
fun isMXRecordValid(host: String): Boolean {
    // Exemplo: Verifica se o dominio do MX Record não é vazio e contém um ponto
    return host.isNotEmpty() && host.contains(".")
}

fun sendValidationEmail(to:String, subject:String, body:String){

    //Configuração do servidor SMTP
    val properties = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
    }

    //Autenticação
    var session = Session.getInstance(properties, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication{
            return PasswordAuthentication("email remetente", "senha do email")
        }
    })

    try {
        //Criação da mensagem
        var message = MimeMessage(session).apply{
            setFrom(InternetAddress("email remetente"))
            setRecipients(RecipientType.TO, InternetAddress.parse(to))
            setSubject(subject)
            setText(body)
        }

        //Envio do email
        Transport.send(message)
        println("Email enviado com sucesso para $to")
    } catch (e: MessagingException){
        e.printStackTrace()
    }

}

fun main() {
    val clients:MutableList<Client> = mutableListOf()
    mainMenu(clients)
}