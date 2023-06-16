# WhatsUp

WhatsUp is a tool for datapacks that allows servers to query a provided web endpoint at a given frequency, and execute
functions (the normal `.mcfunction` type) based on the results returned by that endpoint.

WhatsUp defines both `listeners` and `predicates`. `listeners` are defined at `data/<namespace>/whatsup/listeners/<name>.json`,
and take the following structure:

* `endpoint` - The URL to be queried by the listener
* `frequency` - An integer representing how many seconds should pass between queries of the endpoint
* `actions` - A list of actions to do based on the returned result of the endpoint. Each takes the following form:
  * `predicate` - The resource location of a predicate to check against the endpoint's result
  * `function` - The resource location of a function to execute if the predicate passes
  * `levels` - (Optional; defaults to `["overworld"]`) A list of dimensions to run the provided function in

Predicates are stored at `data/<namespace>/whatsup/predicates/<name>.groovy`, and are groovy scripts. The groovy environment
the predicates run in has access to the `context` variable, which has the following properties:
* `text` - The unprocessed response text
* `json` - The a Map of the response parsed as JSON, or `null` if the response is not a JSON object

An example datapack can be found in `Common/src/test/resources`, or in the additional files on Modrinth or Curseforge
