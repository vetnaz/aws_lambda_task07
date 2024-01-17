package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;

import org.joda.time.DateTime;
import com.task07.dto.Uudi;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(lambdaName = "uuid_generator",
        roleName = "uuid_generator-role"
)
@RuleEventSource(targetRule = "uuid_trigger")
@DependsOn(name = "uuid_trigger", resourceType = ResourceType.CLOUDWATCH_RULE)
public class UuidGenerator implements RequestHandler<Object, Map<String, Object>> {

    private static final String BUCKET_NAME = "cmtr-985d4752-uuid-trigger-test";
    private static final String REGION = "eu-central-1";

    public Map<String, Object> handleRequest(Object request, Context context) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Uudi uudi = new Uudi();
        uudi.setIds(generateRandomUUIDs(10));
        AmazonS3 amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(REGION)
                .build();
        String fileName = DateTime.now() + ".txt";

        amazonS3.putObject(BUCKET_NAME, fileName, gson.toJson(uudi));

        return null;
    }

    private static ArrayList<String> generateRandomUUIDs(int count) {
        ArrayList<String> uuids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            uuids.add(UUID.randomUUID().toString());
        }
        return uuids;
    }
}
