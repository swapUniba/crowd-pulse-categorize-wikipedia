crowd-pulse-categorize-wikipedia
================================

Crowd Pulse Wikipedia tag categorizer.

----------------------

The `wikipedia` plugin tries to associate Wikipedia categories to non-stop-word tags 
in the Messages flowing through the stream.

You can specify the configuration option "calculate" with one of the following values:
- all: categorize all tags coming from the stream;
- new: categorize the tags with no categories (property is null or array is empty);

Example of usage:
```json
{
  "process": {
    "name": "wikipedia-tester",
    "logs": "/opt/crowd-pulse/logs"
  },
  "nodes": {
    "fetch": {
      "plugin": "message-fetch",
      "config": {
        "db": "test-wikipedia"
      }
    },
    "wiki": {
      "plugin": "wikipedia",
      "config": {
        "calculate": "new"
      }
    },
    "persistance": {
      "plugin": "message-persist",
      "config": {
        "db": "test-wikipedia"
      }
    }
  },
  "edges": {
    "fetch": [
      "wiki"
    ],
    "wiki": [
      "persistance"
    ]
  }
}
```