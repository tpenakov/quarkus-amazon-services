exports.handler = async (event) => {
    let body = JSON.parse(event.body)
    const name = body.name
    const response = {
        statusCode: 200,
        body: "Hello " + name + "!",
    };
    return response;
};
