package work.trapezoid
import com.slack.api.Slack
import com.slack.api.SlackConfig
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import com.slack.api.model.block.ActionsBlock.ActionsBlockBuilder
import com.slack.api.model.block.Blocks.{actions, asBlocks, divider, section}
import com.slack.api.model.block.composition.BlockCompositions.{markdownText, plainText}
import com.slack.api.model.block.composition.PlainTextObject.PlainTextObjectBuilder
import com.slack.api.model.block.element.BlockElements.{asElements, button}

import com.slack.api.bolt.{App => BoltApp}
import com.slack.api.bolt.jetty.SlackAppServer

object App {

   def main(args : Array[String]) {
      val config:SlackConfig = new SlackConfig();
      config.setPrettyResponseLoggingEnabled(true);

      val slack = Slack.getInstance(config)
      val token = System.getenv("SLACK_TOKEN");
      val methods:MethodsClient = slack.methods(token)

      val response:ChatPostMessageResponse = methods.chatPostMessage(ChatPostMessageRequest.builder.channel("#chillbot").blocks(
         asBlocks(
            divider(),
            section(section => section.text(markdownText(":zap: *Now online*"))),
            actions((actions:ActionsBlockBuilder) => actions.elements(asElements(
                  button(b => b.text(plainText((pt:PlainTextObjectBuilder) => pt.emoji(true).text("Alpha"))).value("v1")),
                  button(b => b.text(plainText((pt:PlainTextObjectBuilder) => pt.emoji(true).text("Beta"))).value("v2"))
               ))
            )
         )
      ).build)
      println("Got response " + response)

      // App expects env variables (SLACK_BOT_TOKEN, SLACK_SIGNING_SECRET)
      val app:BoltApp = new BoltApp()

      app.command("/chill", (req, ctx) => {
        // println("Got command /hello" + req)
        // ctx.logger.info("respond result - {}", req)
        // val res = ctx.respond(it.text(":wave: Hello!"))
        ctx.ack(":wave: Hello!")
      })

      val server:SlackAppServer = new SlackAppServer(app, 3000)
      server.start() // http://localhost:3000/slack/events

      // val rtm = slack.rtm(token)
      // rtm.connect()
   }

}
