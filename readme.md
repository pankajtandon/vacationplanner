Multi Dimensional Planner
===

This project demonstrates how we can use
Spring AI's implementation of OpenAI's Function
Calling feature along with Retrieval Augumented Generation (RAG) so that we can not only interact 
with pre-trained models in a secure manner, we can also minimize the 
tokens of function metadata sent to the LLMs.

These are sample questions that this app can answer, by combining Function Calling and RAG:
```
Q1. I live in Pittsburgh, PA and I love golf.
In the fall of 2024, where should I fly to, in Europe or the United States, to play, 
where the weather is pleasant and it's economical too?

Q2. I need to remain healthy by following a vegetarian diet and also remain under
budget. What dishes can you recommend so that I follow the 
90 30 50 diet rule and such that it is also affordable over a year?

```

#### To build

```
./mvnw clean install
```


#### To run the test that demonstrates how function calling works

Create the following environment variables in your .zshrc/bashrc:

```
export OPENAI_API_KEY=[api key (Create at https://platform.openai.com/api-keys)]
export VIRTUALCROSSING_API_KEY=[api key (Create at https://www.visualcrossing.com/account)]
export AMADEUS_CLIENT_ID=[api client Id (Create at https://www.accounts.amadeus.com/)]
export AMADEUS_CLIENT_SECRET=[api client secret (Create at https://www.accounts.amadeus.com/)]
```

- Supply an API Key for OpenAI (LLM and embedding), VisualCrossing (Weather API), Amadeus (FlightService) via

 ```
  -Dspring.ai.openai.apiKey=${OPENAI_API_KEY}
  -Dweather.visualcrossing.apiKey=${VISUALCROSSING_API_KEY}
  -Dflight.amadeus.client-id=${AMADEUS_CLIENT_ID}
  -Dflight.amadeus.client-secret=${AMADEUS_CLIENT_SECRET}
  ```
- Run the test

  ```
    ./mvnw clean test
  ```
  You should see log output on the console that indicates how the LLM invokes your functions and infers data based on the results 
  returned by your custom functions.


#### Running the webapp
This app uses `elasticsearch` vector db for the RAG phase. (This is needed becase the much simpler file system based `SimpleVectorStore`
implementation, does not support meta-data filtering which is needed by this example to support RAG.)
It expects the elasticsearch db to be up on port 9200.
The app uses `docker` to bring up elastic using `docker-compose` in the project root.

After you have built the project, to execute it, there are two options:

1. Bring up elastic search docker containers explicitly:
From the project root:

```
docker-compose up

# When the containers are up:
java -jar target/vacationplanner-0.0.1-SNAPSHOT.jar --spring.ai.openai.apiKey=${OPENAI_API_KEY} --weather.visualcrossing.apiKey=${VISUALCROSSING_API_KEY} --flight.amadeus.client-id=${AMADEUS_CLIENT_ID} --flight.amadeus.client-secret=${AMADEUS_CLIENT_SECRET}
```

2. Use the `spring-boot-docker-compose` starter to integrate docker with the maven lifecycle.
In this case, install `mvn` and the project source and then from the project root, execute:

```
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments='-Dspring.ai.openai.apiKey=${OPENAI_API_KEY} -Dweather.visualcrossing.apiKey=${VISUALCROSSING_API_KEY} -Dflight.amadeus.client-id=${AMADEUS_CLIENT_ID} -Dflight.amadeus.client-secret=${AMADEUS_CLIENT_SECRET}'
```
This automatically starts the docker containers before starting the application and also stops it when 
bringing the app down.

When the app is ready to receive traffic, you should see:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.0)

20240710T085833.785 INFO  [main] o.s.b.StartupInfoLogger {} Starting VacationplannerApplication using Java 17.0.7 with PID 52397 (/Users/pankaj/myProjects/ml/vacationplanner/target/classes started by pankaj in /Users/pankaj/myProjects/ml/vacationplanner)
20240710T085833.791 DEBUG [main] o.s.b.StartupInfoLogger {} Running with Spring Boot v3.3.0, Spring v6.1.8
20240710T085833.792 INFO  [main] o.s.b.SpringApplication {} No active profile set, falling back to 1 default profile: "default"
20240710T085833.976 INFO  [main] o.s.b.d.c.l.DockerComposeLifecycleManager {} Using Docker Compose file '/Users/pankaj/myProjects/ml/vacationplanner/docker-compose.yml'
20240710T085834.803 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container es01  Created
20240710T085834.803 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container kib01  Created
20240710T085834.808 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container kib01  Starting
20240710T085834.808 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container es01  Starting
20240710T085835.079 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container es01  Started
20240710T085835.104 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container kib01  Started
20240710T085835.105 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container es01  Waiting
20240710T085835.105 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container kib01  Waiting
20240710T085835.610 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container kib01  Healthy
20240710T085835.611 INFO  [OutputReader-stderr] o.s.b.l.LogLevel {}  Container es01  Healthy
20240710T085856.045 INFO  [main] o.s.b.w.e.t.TomcatWebServer {} Tomcat initialized with port 8080 (http)
20240710T085856.053 INFO  [main] o.a.j.l.DirectJDKLog {} Initializing ProtocolHandler ["http-nio-8080"]
20240710T085856.054 INFO  [main] o.a.j.l.DirectJDKLog {} Starting service [Tomcat]
20240710T085856.054 INFO  [main] o.a.j.l.DirectJDKLog {} Starting Servlet engine: [Apache Tomcat/10.1.24]
20240710T085856.092 INFO  [main] o.a.j.l.DirectJDKLog {} Initializing Spring embedded WebApplicationContext
20240710T085856.094 INFO  [main] o.s.b.w.s.c.ServletWebServerApplicationContext {} Root WebApplicationContext: initialization completed in 887 ms
20240710T085856.963 INFO  [main] o.s.b.a.w.s.WelcomePageHandlerMapping {} Adding welcome page: class path resource [static/index.html]
20240710T085857.457 INFO  [main] o.s.b.a.e.w.EndpointLinksResolver {} Exposing 15 endpoints beneath base path '/actuator'
20240710T085857.514 INFO  [main] o.a.j.l.DirectJDKLog {} Starting ProtocolHandler ["http-nio-8080"]
20240710T085857.526 INFO  [main] o.s.b.w.e.t.TomcatWebServer {} Tomcat started on port 8080 (http) with context path '/planner'
20240710T085857.562 INFO  [main] o.s.b.StartupInfoLogger {} Started VacationplannerApplication in 24.029 seconds (process running for 25.268)
```
#### Interacting with the Webapp:
```
curl -H "content-type: application/json" -X POST http://localhost:8080/planner/query -d '{"userQuery": "I live in Pittsburgh, PA and I love golf. In the fall of 2024, where should I fly to, in Europe or the United States, to play, where the weather is pleasant and it'\''s economical too?", "userSuppliedTopKFunctions": 4}'
```

The second parameter in the JSON payload (`userSuppliedTopKFunctions`) is optional and represents the number of functions' metadata that the application should send to the LLM based on the query asked. The higher the number, the more metadata is sent to the LLM and the more tokens will be spent.
If it is not specified, the value used is in the property `rag.topK`.


#### Sample output
Below is sample output that has been produced by the run of the above using OpenAI:

The question asked is:

```
I live in Pittsburgh, PA and I love golf. In the fall of 2024, where should I fly to, in Europe or the United States, to play, where the weather is pleasant and it's economical too?
```

Here is the response from OpenAI's GPT LLM after it has gathered information from the custom supplied functions.
```
For a golf trip in October 2024, between New York, NY, and London, UK, here are your options considering weather and airfare:

### Weather in October 2024
- **New York, NY**
  - Average Temperature: 58.17°F
  - Min Average Temperature: 52.40°F
  - Max Average Temperature: 64.46°F

- **London, UK**
  - Average Temperature: 12.93°C (55.27°F)
  - Min Average Temperature: 10.25°C (50.46°F)
  - Max Average Temperature: 15.89°C (60.61°F)

### Airfare (to Destination from Pittsburgh, PA)
- **New York, NY**: €91.39
- **London, UK**: €210.34

### Recommendation
Considering both the weather and airfare, **New York, NY** offers a slightly warmer and more pleasant climate for golf in October 2024 compared to London, UK. Additionally, it's significantly more economical in terms of airfare to fly to New York from Pittsburgh, PA.

Therefore, for a golf trip in the fall of 2024 that's both pleasant in terms of weather and economical, New York, NY would be the recommended destination over London, UK
```

#### What's happening under the covers:

Looking at the logs, we can see the following:

```
20240709T121746.336 INFO  [http-nio-8080-exec-6] c.t.a.v.s.RagService {} There were 5 ragCandidate beans defined in the context out of which 0 were vectorized and inserted into the vectorStore (possibly because they already existed in the vector store)
20240709T121749.020 INFO  [http-nio-8080-exec-6] c.t.a.v.s.RagService {} There were 4 functions found that were relevant to the passed in query, with a distance range from 0.6414832 to 0.7153696
20240709T121749.021 DEBUG [http-nio-8080-exec-6] c.t.a.v.s.RagService {} Functions metadata being sent to LLM in descending order of relevance: [financialService, airfareService, weatherService, currencyExchangeService]
20240709T121819.361 INFO  [http-nio-8080-exec-6] c.t.a.v.s.WeatherService {} Called WeatherService with request: Request[location=New York, NY, lat=40.7128, lon=-74.006, unit=F, month=October, year=2024]
20240709T121819.597 INFO  [http-nio-8080-exec-6] c.t.a.v.s.WeatherService {} WeatherService returned: Response[temp=58.167741935483875, temp_min=52.403225806451616, temp_max=64.45806451612903, unit=F]
20240709T121819.598 INFO  [http-nio-8080-exec-6] c.t.a.v.s.WeatherService {} Called WeatherService with request: Request[location=London, UK, lat=51.5074, lon=-0.1278, unit=C, month=October, year=2024]
20240709T121819.630 INFO  [http-nio-8080-exec-6] c.t.a.v.s.WeatherService {} WeatherService returned: Response[temp=55.270967741935486, temp_min=50.458064516129035, temp_max=60.60645161290322, unit=F]
20240709T121819.630 INFO  [http-nio-8080-exec-6] c.t.a.v.s.AirfareService {} Called AirfareService with Request[origin=Pittsburgh, PA, destination=New York, NY, currency=USD, originLatitude=40.4406, originLongitude=-79.9959, destinationLatitude=40.7128, destinationLongitude=-74.006, month=October, year=2024]
20240709T121824.090 INFO  [http-nio-8080-exec-6] c.t.a.v.s.AirfareService {} AirfareService response: Response[airfare=91.39, currency=EUR]
20240709T121824.091 INFO  [http-nio-8080-exec-6] c.t.a.v.s.AirfareService {} Called AirfareService with Request[origin=Pittsburgh, PA, destination=London, UK, currency=USD, originLatitude=40.4406, originLongitude=-79.9959, destinationLatitude=51.5074, destinationLongitude=-0.1278, month=October, year=2024]
20240709T121829.385 INFO  [http-nio-8080-exec-6] c.t.a.v.s.AirfareService {} AirfareService response: Response[airfare=210.34, currency=EUR]
20240709T121839.927 INFO  [http-nio-8080-exec-6] c.t.a.v.s.VacationService {} Returned a recommendation!


```
Note that the `RecipeService` metadata is _NOT_ sent to the LLM because it is not relevant to the query.
However, if the query is changed to 'Where should I fly during summer to be healthy'

#### Adding a new service.

Because of the way Spring-AI has implemented function calling, adding a new service is extremely easy:

- Write a service that implements `Function<Request, Response>`. Or add this interface to an existing Spring managed service.
- Use the `WeatherService` as an example.
- Ensure that the @JsonClassDescription on the `Request` describes what this function does. (This constitutes the metadata sent to the LLM)
- Ensure that the @JsonPropertyDescription describes each input argument. (This constitutes the metadata sent to the LLM)
- The `apply` method of that service will take in a `Request` and return a `Response`. Implement it appropriately.
- If you would like to use the RAG feature where only the metadata of functions that are relevant to the query is sent to the LLM, 
ensure that the service is annotated with @RagCandidate.
- The Spring Boot application can contain services that do _not_ implement `Function<Request, Response>` or `RagCandidate`. 
- Note that the `Function<Request, Response>` interface and the `RagCandidate` annotation can be added to an existing
Spring service.
- Configure these services as is done in <code>FunctionCallingConfig</code>
- Invoke the <code>OpenAiChatClient</code> as is done in <code>VacationService</code>
- Specify the <code>apiKey</code> and the <code>model</code> in the appropriate `-D propertiers on the CLI` as 
  has been defined in the <code>application.yml</code> file.



#### Using RAG
Combining RAG with function calling helps in reducing the number of tokens 
that are sent to the LLM in the form of function metadata (which can also become large).

The overall idea is that your application can implement a
library of services (aka functions) that can be invoked
selectively by the LLM based on the query.

A second question that uses a different set of functions could be:

```
I need to remain healthy by following a vegetarian diet and also under budget. 
What dishes can you recommend so that I follow the 
90 30 50 diet rule and such that it is also affordable
over a year?
```

This returns the following recommendation:

```
Based on the nutritional content and cost details of the vegetarian dishes evaluated, here are some recommendations that align closely with the 90 30 50 diet rule and are budget-friendly over a year:

1. **Lentil Soup**
   - **Protein:** 25%
   - **Carbs:** 40%
   - **Fat:** 35%
   - **Calories:** 650
   - **Cost:** $20.5 per serving

2. **Quinoa and Black Beans**
   - **Protein:** 24%
   - **Carbs:** 26%
   - **Fat:** 50%
   - **Calories:** 750
   - **Cost:** $15.0 per serving

3. **Tofu Curry**
   - **Protein:** 24%
   - **Carbs:** 26%
   - **Fat:** 50%
   - **Calories:** 750
   - **Cost:** $15.0 per serving

While the "Quinoa and Black Beans" and "Tofu Curry" dishes closely adhere to the intended diet rule and are the most affordable options, you might need to adjust portions or incorporate low-cost supplementary items to meet exact macronutrient targets.

Given your monthly income is $1,000, and assuming other expenses allow, choosing cost-efficient meals like "Quinoa and Black Beans" or "Tofu Curry" could be sustainable. 

**Budget Analysis Over a Year:**

Assuming two servings per day for the sake of simplicity:
- Daily cost (for the most affordable options at $15.0 per serving): $30
- Monthly cost: $900
- Annual cost: $10,800

This seems to exceed your regular monthly income. However, with wise budgeting or considering a less strict adherence by mixing in more affordable ingredients or meals, you could manage to align your diet with your financial constraints more comfortably. 

Remember, variety in your diet is essential for health, so consider these dishes as part of a larger assortment of meals to ensure you're meeting all your nutritional needs without monotony.
```


And here is what is happening under the covers:

```
20240709T120831.175 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RagService {} There were 5 ragCandidate beans defined in the context out of which 0 were vectorized and inserted into the vectorStore (probably because they already existed in the vector store)
20240709T120832.198 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RagService {} There were 2 functions found that were relevant to the passed in query, with a distance range from 0.71621394 to 0.7551434
20240709T120832.199 DEBUG [http-nio-8080-exec-2] c.t.a.v.s.RagService {} Functions metadata being sent to LLM in descending order of relevance: [recipeService, financialService]
20240709T120834.637 INFO  [http-nio-8080-exec-2] c.t.a.v.s.FinancialService {} Called FinancialService with Request[accountNumber=123456]
20240709T120834.640 INFO  [http-nio-8080-exec-2] c.t.a.v.s.FinancialService {} FinancialService response: Response[bankBalance=4500.0, monthlyIncome=1000.0]
20240709T120849.790 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} Called RecipeService with Request[dishName=Lentil Soup]
20240709T120849.791 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} RecipeService response: Response[proteinPercent=25.0, carbPercent=40.0, fatPercent=35.0, calories=650.0, cost=20.5]
20240709T120849.792 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} Called RecipeService with Request[dishName=Chickpea Salad]
20240709T120849.793 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} RecipeService response: Response[proteinPercent=25.0, carbPercent=30.0, fatPercent=45.0, calories=800.0, cost=23.0]
20240709T120849.793 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} Called RecipeService with Request[dishName=Vegetable Stir Fry]
20240709T120849.793 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} RecipeService response: Response[proteinPercent=20.0, carbPercent=35.0, fatPercent=45.0, calories=350.0, cost=23.5]
20240709T120849.794 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} Called RecipeService with Request[dishName=Quinoa and Black Beans]
20240709T120849.794 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} RecipeService response: Response[proteinPercent=24.0, carbPercent=26.0, fatPercent=50.0, calories=750.0, cost=15.0]
20240709T120849.795 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} Called RecipeService with Request[dishName=Tofu Curry]
20240709T120849.795 INFO  [http-nio-8080-exec-2] c.t.a.v.s.RecipeService {} RecipeService response: Response[proteinPercent=24.0, carbPercent=26.0, fatPercent=50.0, calories=750.0, cost=15.0]
20240709T120909.906 INFO  [http-nio-8080-exec-2] c.t.a.v.s.VacationService {} Returned a recommendation!

```
Note that the `AirfareService`, `WeatherService` and `CurrencyExchangeService` metadata is _NOT_ sent to the LLM for this query.

Thus, we have combined the powerful function calling feature with the RAG technique in this application.
