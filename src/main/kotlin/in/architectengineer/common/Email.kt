package `in`.architectengineer.common

import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.auth.SesAuthSchemeParameters
import aws.sdk.kotlin.services.ses.auth.SesAuthSchemeProvider
import aws.sdk.kotlin.services.ses.model.*
import aws.sdk.kotlin.services.ses.sendTemplatedEmail
import aws.smithy.kotlin.runtime.auth.AuthOption

object Email{

   private val sesClient =  SesClient {
       region = "ap-south-1"
   }

    suspend fun sendEmail(
        from: String,
        to: String,
        toName: String,
        fromName: String,
        sub: String,
        body: String,
        htmlBody: String
    ) {
        try {
            val sender = "$fromName <$from>"
            val recipient = "$toName <$to>"

            val sendEmailRequest = SendEmailRequest {
                source = sender
                destination = Destination {
                    toAddresses = listOf(recipient)
                }
                message = Message {
                    subject = Content {
                        data = sub
                    }
                    this.body = Body {
                        text {
                            data = body
                        }
                        html {
                            data = htmlBody
                        }
                    }
                }
            }
            sesClient.sendEmail(sendEmailRequest)
        }catch (e:Exception){
            throw e
        }
    }
}