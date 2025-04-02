<div align="center">

# Speed.AI - LLM Benchmarking

**Easy benchmarking of LLMs using Llama.cpp engine**

</div>

<div align="center">
  <img src="https://github.com/user-attachments/assets/ec7a7f18-50b6-4727-9485-8c8e2127663c" alt="Performance Results" width="20%"/>
  <img src="https://github.com/user-attachments/assets/1ba32f09-1758-4193-ab58-a110fd79d3f0" alt="Performance Results" width="20%"/>
  <img src="https://github.com/user-attachments/assets/0608f8dc-0982-4e48-8599-3d9f463c5bbd" alt="Benchmarking Screen" width="20%"/>
  <img src="https://github.com/user-attachments/assets/a9d7c167-c48a-4b53-a0b2-6d22a6875af5" alt="Performance Results" width="20%"/>
</div>

### About 

Speed.AI is an Android app for benchmarking locally run LLM models using the Llama.cpp engine. You can also chat with LLMs and view detailed benchmarking data.

### Setup

Clone the repository and initialize its submodules:

```sh
git clone https://github.com/TIC-13/llm-benchmarking-lcpp.git
cd llm-benchmarking-lcpp
git submodule update --init --recursive
```

### Benchmark Ranking

Benchmarking results are sent to a ranking system, where you can compare performance across different devices. The ranking is shared with the [Speed.AI - AI Benchmarking](https://github.com/TIC-13/benchmarking-ai-v2) app.

To host your own ranking instance, check out these repositories:
- [Frontend](https://github.com/TIC-13/benchmark-ranking-front)
- [Backend](https://github.com/TIC-13/benchmark-ranking-back)

Hosting a ranking instance is optional; the app works independently.

### Configuring the Environment

To connect the app to a ranking instance, configure the following environment variables in `local.properties`:
- `API_ADDRESS`: Backend address
- `API_KEY`: A base64-encoded 32-byte (AES-256) key (must match the backend key)
- `RANKING_ADDRESS`: URL of the ranking server

### Adding New Models

To add a new model, edit `LLMViewModel.kt` and append its Hugging Face `.gguf` download URL to the `huggingFaceUrls` list.

### Technologies Used

The inference engine in this app is taken from [SmolChat](https://github.com/shubham0204/SmolChat-Android).

