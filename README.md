# AiCodeCompletion
AI Code Completion plugin for Netbeans IDE (Like copilot, but more primivite)

Features:
* Simple fill-in-the-middle (using netbeans code completion)
* Instruction based. Ctrl+Shift+G to bring up dialog
* Chat with code (active file) appended

(Tool usage with code insertion is wip, but not working atm)

[NBM file available here]([https://github.com/neph1/AiCodeCompletion/blob/main/build/com-mindemia-aicodecompletion.nbm](https://github.com/neph1/NetbeansAiCodeCompletion/blob/main/com-mindemia-aicodecompletion.nbm)) (unsigned): 

Fill-in-the-middle example generated using llama.cpp with Qwen2.5 1.5b (Q8)

![ai_completion](https://github.com/user-attachments/assets/b6eaf6fd-9cae-4d4d-bea7-ef910d03f58c)


Chat example generated using llama.cpp with Qwen2.5 8b

![image](https://github.com/user-attachments/assets/07585c8a-9566-4f0f-b2a3-b14375978e45)



While the plugin uses http to call any back end, its prompt and request may not suit all models or providers.

Uses the code completion function and is prompted using ctrl+space. You can then select the top-most option with enter (no "ghost code")

Does not provide models or backend. It's simply routing to wherever you get your tokens.
