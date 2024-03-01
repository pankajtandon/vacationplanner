Vacation Planner
===

This project demonstrates how we can use
Spring AI's implementation of OpenAI's Function
Calling feature.

#### To build

```agsl
./mvnw clean install
```


#### To run the test that demonstrates how function calling works

- Supply an API Key for OpenAI via
   ```
  -Dspring.ai.openai.apiKey=<your key here>
   ```
- Run the test
  ```agsl
  VacationServiceTests
  ```
  You should see log output on the console that indicates how the LLM invokes your functions and infers data based on the results 
  returned by your custom functions.
#### Explore
- The functions that you need to provide that _may_ be called by 
  the model can be modelled after <code>WeatherService</code> or <code>AirfareService</code>
- Configure these services as is done in <code>FunctionCallingConfig</code>
- Invoke the <code>OpenAiChatClient</code> as is done in <code>VacationService</code>
- Specify the <code>apiKey</code> and the <code>model</code> in the appropriate <code>application.yml</code> file as 
  has been done in <code>application.yml</code> file.

  That's all there is to it!