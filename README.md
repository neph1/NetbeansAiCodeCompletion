# AiCodeCompletion
AI Code Completion plugin for Netbeans IDE (Like copilot, but more primivite)

Features:
* Simple fill-in-the-middle (using netbeans code completion)
* Instruction based. Ctrl+Shift+G to bring up dialog
* Chat with code (active file) appended

(Tool usage with code insertion is wip, but not working atm)


![ai_completion](https://github.com/user-attachments/assets/b6eaf6fd-9cae-4d4d-bea7-ef910d03f58c)

Example generated using llama.cpp with Qwen2.5 1.5b (Q8)

![image](https://github.com/user-attachments/assets/fbc1f7a0-62fa-497d-88b8-aa55fe23eb5a)

Example generated using llama.cpp with Qwen2.5 8b

While the plugin uses http to call any back end, its prompt and request may not suit all models or providers.

Uses the code completion function and is prompted using ctrl+space. You can then select the top-most option with enter (no "ghost code")

Does not provide models or backend. It's simply routing to wherever you get your tokens.
