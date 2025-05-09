# AiCodeCompletion
AI Code Completion plugin for Netbeans IDE (Like copilot, but more primivite)


![ai_completion](https://github.com/user-attachments/assets/b6eaf6fd-9cae-4d4d-bea7-ef910d03f58c)

Example generated using llama.cpp with Qwen2.5 1.5b (Q8)

While the plugin uses http to call any back end, its prompt and request may not suit all models or providers.

Uses the code completion function and is prompted using ctrl+space. You can then select the top-most option with enter (no "ghost code")

Does not provide models or backend. It's simply routing to wherever you get your tokens.
