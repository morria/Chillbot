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

import org.slf4j.{Logger, LoggerFactory}

/**
 * App expects env variables SLACK_TOKEN, SLACK_BOT_TOKEN
 * and SLACK_SIGNING_SECRET.
 *
 * SLACK_TOKEN and SLACK_BOT_TOKEN will probably be the same.
 */
object App {

  private val logger: Logger = LoggerFactory.getLogger(classOf[App])

   def main(args : Array[String]) {
      val config:SlackConfig = new SlackConfig();

      System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.JavaUtilLog");
      System.setProperty("org.eclipse.jetty.util.log.class.LEVEL", "DEBUG");
      System.setProperty("org.slf4j.simpleLogger.log.com.slack.api", "debug");


      config.setPrettyResponseLoggingEnabled(true);

      val slack = Slack.getInstance(config)
      val token = System.getenv("SLACK_TOKEN");
      val methods:MethodsClient = slack.methods(token)

      val response:ChatPostMessageResponse = methods.chatPostMessage(ChatPostMessageRequest.builder.channel("#chillbot").blocks(
         asBlocks(
            divider(),
            section(section => section.text(markdownText(":zap: *Now online*"))),
            actions((actions:ActionsBlockBuilder) => actions.blockId("now-online").elements(asElements(
                  button(b=> b.actionId("chillbot-action-alpha").text(plainText((pt:PlainTextObjectBuilder) => pt.emoji(true).text("Alpha"))).value("v1")),
                  button(b => b.actionId("chillbot-action-beta").text(plainText((pt:PlainTextObjectBuilder) => pt.emoji(true).text("Beta"))).value("v2"))
               ))
            )
         )
      ).build)
      println("Got response " + response)

      // App expects env variables (SLACK_BOT_TOKEN, SLACK_SIGNING_SECRET)
      val app:BoltApp = new BoltApp()

      app.command("/chill", (req, ctx) => {
        logger.info("Got /chill");
        logger.debug("Got /chill");
        ctx.ack(":wave: Hello!")
      })

      // when a user clicks a button in the actions block
      app.blockAction("chillbot-action-alpha", (req, ctx) => {
        logger.info("Got chillbot-action-alpha");
        val value:String  = req.getPayload().getActions().get(0).getValue(); // "button's value"
        if (req.getPayload().getResponseUrl() != null) {
          // Post a message to the same channel if it's a block in a message
          ctx.respond("Alpha: " + value)
        }
        ctx.respond("Alpha => " + value)
        ctx.ack()
      });

      // when a user clicks a button in the actions block
      app.blockAction("chillbot-action-beta", (req, ctx) => {
        logger.info("Got chillbot-action-beta");
        val value:String  = req.getPayload().getActions().get(0).getValue(); // "button's value"
        if (req.getPayload().getResponseUrl() != null) {
          // Post a message to the same channel if it's a block in a message
          ctx.respond("Beta: " + value)
        }
        ctx.respond("Beta => " + value)
        ctx.ack()
      });


      val server:SlackAppServer = new SlackAppServer(app, 3000)
      server.start() // http://localhost:3000/slack/events

      // val rtm = slack.rtm(token)
      // rtm.connect()
   }
}
