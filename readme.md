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
    -Dspring.ai.openai.apiKey=<Your Key here>
    -Dweather.visualcrossing.apiKey=<Your Key here>
    -Dflight.amadeus.client-id=<Your Key here>
    -Dflight.amadeus.client-secret=<Your Key here>
    ```
- Run the test
  ```agsl
  VacationServiceTests
  ```
  You should see log output on the console that indicates how the LLM invokes your functions and infers data based on the results 
  returned by your custom functions.


#### Running on the command line

After you have built the project, execute:

```
java -jar target/vacationplanner-0.0.1-SNAPSHOT.jar "I live in Pittsburgh, PA and I love golf. In the summer of 2024, where should I fly to, in Europe or the United States, to play, where the weather is pleasant and it's economical too?" --spring.ai.openai.apiKey=[Your key here] --weather.visualcrossing.apiKey=[Your key here] --flight.amadeus.client-id=[Your key here] --flight.amadeus.client-secret=[Your key here]
```

As you can see, the question has been pased on the command line in quotes and the ApiKeys have also been passed as parameters using the `--` syntax.


#### Sample output
Below is sample output that has been produced by the run of the above:

The question asked is:

```agsl
I live in Pittsburgh, PA and I love golf.
In the summer of 2024, where should I fly to in Europe or the United States, to play, where the weather
is pleasant and it's economical too?
```
Here is the response from OpenAI's GPT LLM after it has gathered information from the custom supplied functions.
```agsl
2024-03-05T22:08:49.180-05:00  INFO 31393 --- [           main] c.t.a.v.service.WeatherService           : Called WeatherService with request: Request[location=London, UK, lat=51.509865, lon=-0.118092, unit=C, month=June, year=2024]
2024-03-05T22:08:49.541-05:00  INFO 31393 --- [           main] c.t.a.v.service.WeatherService           : Called WeatherService with request: Request[location=Paris, France, lat=48.8566, lon=2.3522, unit=C, month=June, year=2024]
2024-03-05T22:08:49.670-05:00  INFO 31393 --- [           main] c.t.a.v.service.WeatherService           : Called WeatherService with request: Request[location=Amsterdam, Netherlands, lat=52.3676, lon=4.9041, unit=C, month=June, year=2024]
2024-03-05T22:08:49.791-05:00  INFO 31393 --- [           main] c.t.a.v.service.WeatherService           : Called WeatherService with request: Request[location=New York, NY, lat=40.7128, lon=-74.006, unit=F, month=June, year=2024]
2024-03-05T22:08:49.901-05:00  INFO 31393 --- [           main] c.t.a.v.service.WeatherService           : Called WeatherService with request: Request[location=Los Angeles, CA, lat=34.0522, lon=-118.2437, unit=F, month=June, year=2024]
2024-03-05T22:08:49.972-05:00  INFO 31393 --- [           main] c.t.a.v.service.WeatherService           : Called WeatherService with request: Request[location=San Francisco, CA, lat=37.7749, lon=-122.4194, unit=F, month=June, year=2024]
2024-03-05T22:09:04.957-05:00  INFO 31393 --- [           main] c.t.a.v.service.AirfareService           : Called AirfareService with Request[origin=Pittsburgh, PA, destination=London, UK, currency=USD, originLatitude=40.4406, originLongitude=-79.9959, destinationLatitude=51.509865, destinationLongitude=-0.118092, month=June, year=2024]
2024-03-05T22:09:10.245-05:00  INFO 31393 --- [           main] c.t.a.v.service.AirfareService           : Called AirfareService with Request[origin=Pittsburgh, PA, destination=Paris, France, currency=USD, originLatitude=40.4406, originLongitude=-79.9959, destinationLatitude=48.8566, destinationLongitude=2.3522, month=June, year=2024]
2024-03-05T22:09:17.904-05:00  INFO 31393 --- [           main] c.t.a.v.service.AirfareService           : Called AirfareService with Request[origin=Pittsburgh, PA, destination=Amsterdam, Netherlands, currency=USD, originLatitude=40.4406, originLongitude=-79.9959, destinationLatitude=52.3676, destinationLongitude=4.9041, month=June, year=2024]
2024-03-05T22:09:49.259-05:00  INFO 31393 --- [           main] c.t.a.v.service.AirfareService           : Called AirfareService with Request[origin=Pittsburgh, PA, destination=New York, NY, currency=USD, originLatitude=40.4406, originLongitude=-79.9959, destinationLatitude=40.7128, destinationLongitude=-74.006, month=June, year=2024]
2024-03-05T22:09:52.536-05:00  INFO 31393 --- [           main] c.t.a.v.service.AirfareService           : Called AirfareService with Request[origin=Pittsburgh, PA, destination=Los Angeles, CA, currency=USD, originLatitude=40.4406, originLongitude=-79.9959, destinationLatitude=34.0522, destinationLongitude=-118.2437, month=June, year=2024]
2024-03-05T22:09:57.249-05:00  INFO 31393 --- [           main] c.t.a.v.service.AirfareService           : Called AirfareService with Request[origin=Pittsburgh, PA, destination=San Francisco, CA, currency=USD, originLatitude=40.4406, originLongitude=-79.9959, destinationLatitude=37.7749, destinationLongitude=-122.4194, month=June, year=2024]
2024-03-05T22:10:09.984-05:00  INFO 31393 --- [           main] c.t.a.v.service.CurrencyExchangeService  : Called CurrencyExchangeService with Request[amount=954.05, currencyIn=EUR, currencyOut=USD]
2024-03-05T22:10:09.986-05:00  INFO 31393 --- [           main] c.t.a.v.service.CurrencyExchangeService  : Called CurrencyExchangeService with Request[amount=931.05, currencyIn=EUR, currencyOut=USD]
2024-03-05T22:10:09.987-05:00  INFO 31393 --- [           main] c.t.a.v.service.CurrencyExchangeService  : Called CurrencyExchangeService with Request[amount=1183.69, currencyIn=EUR, currencyOut=USD]
2024-03-05T22:10:09.987-05:00  INFO 31393 --- [           main] c.t.a.v.service.CurrencyExchangeService  : Called CurrencyExchangeService with Request[amount=217.14, currencyIn=EUR, currencyOut=USD]
2024-03-05T22:10:09.987-05:00  INFO 31393 --- [           main] c.t.a.v.service.CurrencyExchangeService  : Called CurrencyExchangeService with Request[amount=221.62, currencyIn=EUR, currencyOut=USD]
2024-03-05T22:10:09.987-05:00  INFO 31393 --- [           main] c.t.a.v.service.CurrencyExchangeService  : Called CurrencyExchangeService with Request[amount=290.24, currencyIn=EUR, currencyOut=USD]
2024-03-05T22:10:35.244-05:00  INFO 31393 --- [           main] c.t.a.v.service.VacationService          : Response: ChatResponse [metadata={ @type: org.springframework.ai.openai.metadata.OpenAiChatResponseMetadata, id: chatcmpl-8zbxSikbaeTvCTDV8EuRlhlzHSsVW, usage: Usage[completionTokens=419, promptTokens=1866, totalTokens=2285], rateLimit: { @type: org.springframework.ai.openai.metadata.OpenAiRateLimit, requestsLimit: 5000, requestsRemaining: 4999, requestsReset: PT0.012S, tokensLimit: 600000; tokensRemaining: 599224; tokensReset: PT0.077S } }, generations=[Generation{assistantMessage=AssistantMessage{content='Based on the weather conditions and airfare costs for June 2024, here's a summary to help you decide where to fly for a pleasant and economical golfing experience:

### Weather Conditions in June 2024:

- **London, UK**: Average temperature of 62.8°F, ranging from 55.9°F to 70.3°F.
- **Paris, France**: Average temperature of 65.7°F, ranging from 57.4°F to 73.8°F.
- **Amsterdam, Netherlands**: Average temperature of 61.0°F, ranging from 54.0°F to 67.6°F.
- **New York, NY**: Average temperature of 71.5°F, ranging from 64.8°F to 78.9°F.
- **Los Angeles, CA**: Average temperature of 67.9°F, ranging from 62.0°F to 76.3°F.
- **San Francisco, CA**: Average temperature of 60.2°F, ranging from 54.6°F to 68.0°F.

### Airfare Costs from Pittsburgh, PA in June 2024 (in USD):

- **London, UK**: $963.59
- **Paris, France**: $940.36
- **Amsterdam, Netherlands**: $1195.53
- **New York, NY**: $219.31
- **Los Angeles, CA**: $223.84
- **San Francisco, CA**: $293.14

### Recommendation:

For a combination of pleasant weather and economical travel, **New York, NY** stands out as the best destination. 
The weather in June is warm and enjoyable for golfing, and the airfare is significantly 
more affordable compared to European destinations. 
If you prefer a slightly cooler climate and don't mind a bit higher but still reasonable airfare, 
**San Francisco, CA** could be another great option. 
Both cities offer excellent golfing opportunities along with the added benefit of 
being closer to Pittsburgh, PA, compared to European destinations.', 
properties={role=ASSISTANT}, messageType=ASSISTANT}, chatGenerationMetadata=org.springframework.ai.chat.metadata.ChatGenerationMetadata$1@687fd6e}]]

```
#### Explore
- The functions that you need to provide that _may_ be called by 
  the model can be modelled after <code>WeatherService</code> or <code>AirfareService</code>
- Configure these services as is done in <code>FunctionCallingConfig</code>
- Invoke the <code>OpenAiChatClient</code> as is done in <code>VacationService</code>
- Specify the <code>apiKey</code> and the <code>model</code> in the appropriate <code>application.yml</code> file as 
  has been done in <code>application.yml</code> file.

  That's all there is to it!