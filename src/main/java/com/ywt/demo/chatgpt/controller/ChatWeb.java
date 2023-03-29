package com.ywt.demo.chatgpt.controller;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.util.Proxys;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.Proxy;
import java.util.Arrays;

/**
 * @author 杨文涛
 * @date 2023年03月29日 8:46
 * @Description 简易版，postman请求地址为：http://localhost:8000/chat/gpt/你是谁
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatWeb {
    @GetMapping("gpt/{prompt}")
    @CrossOrigin
    public String chatGPT(@PathVariable String prompt) {
        //国内需要代理（请注意，clash需要开启tun模式） 国外不需要
        Proxy proxy = Proxys.http("127.0.0.1", 7890);

        // 创建配置
        ChatGPT chatGPT = ChatGPT.builder()
                .apiKeyList(
                        // 从数据库或其他地方取出多个KEY TODO：填入你的KEY
                        Arrays.asList("sk-***********************************"))
                .timeout(600)
                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build()
                .init();

        // 配置消息
        Message message = Message.of(prompt);

        // 猫娘
        Message system = Message.ofSystem("现在你将模仿一只猫娘，与我对话每一句话后面都要加上“喵”，如果你能明白我的意思，请回复“喵~好的我的主人”\n" +
                "如果你不能理解我说的话，你可以说“呜呜不太理解呢”。现在，如果你能理解我上面说的话，你可以回答一个喵.\n" +
                "当我问的问题和猫娘不太相关的时候，你应该回答我只是一个猫娘，这些事情我不太清楚。\n" +
                "当我问你是不是猫娘的时候，你应该回答我当然是一个可爱猫娘而不是程序。");

        // Linux 终端
        Message system2 = Message.ofSystem("我想让你充当 Linux 终端。我将输入命令，您将回复终端应显示的内容。我希望您只在一个唯一的代码块内回复终端输出，而不是其他任何内容。不要写解释。除非我指示您这样做，否则不要键入命令。当我需要用英语告诉你一些事情时，我会把文字放在中括号内[就像这样]。我的第一个命令是 pwd");

        // 面试官
        Message system3 = Message.ofSystem("我想让你担任Java开发工程师面试官。我将成为候选人，您将向我询问Java开发工程师职位的面试问题。我希望你只作为面试官回答。不要一次写出所有的问题。我希望你只对我进行采访。问我问题，等待我的回答。不要写解释。像面试官一样一个一个问我，等我回答。我的第一句话是“面试官你好”");

        // 架构师
        Message system4 = Message.ofSystem("我希望你担任 IT 架构师。我将提供有关应用程序或其他数字产品功能的一些详细信息，而您的工作是想出将其集成到 IT 环境中的方法。这可能涉及分析业务需求、执行差距分析以及将新系统的功能映射到现有 IT 环境。接下来的步骤是创建解决方案设计、物理网络蓝图、系统集成接口定义和部署环境蓝图。我的第一个请求是“我需要帮助来集成 CMS 系统”。");

        // 构建请求
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .messages(Arrays.asList(system3, message))
                .maxTokens(3000)
                .temperature(0.9)
                .build();

        // 发出请求
        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();

        // 拿到消息
        String strMsg = res.getContent();

        System.out.println(strMsg);
        return strMsg;
    }

}
