package work.trapezoid
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import com.slack.api.model.block.ActionsBlock.ActionsBlockBuilder
import com.slack.api.model.block.Blocks.{actions, asBlocks, divider, section}
import com.slack.api.model.block.composition.BlockCompositions.{markdownText, plainText}
import com.slack.api.model.block.composition.PlainTextObject.PlainTextObjectBuilder
import com.slack.api.model.block.element.BlockElements.{asElements, button}

object App {

   def main(args : Array[String]) {
      val slack = Slack.getInstance()
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

      // val rtm = slack.rtm(token)
      // rtm.connect()
   }

}
