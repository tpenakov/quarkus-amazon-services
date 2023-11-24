package io.quarkus.it.amazon;

import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.it.amazon.lambda.LambdaResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
public class AmazonLambdaTest {

    byte[] helloLambdaZipArchive;

    @BeforeAll
    public void setup() throws IOException {

        // zip the index.js file
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos)) {
            String filename = "index.js";
            ZipEntry entry = new ZipEntry(filename);
            zos.putNextEntry(entry);
            zos.write(IOUtils
                    .toByteArray(Optional.ofNullable(getClass().getResourceAsStream("/lambda/index.js"))
                            .orElseThrow()));
            zos.closeEntry();
            zos.close();

            // this is the zip file as byte[]
            helloLambdaZipArchive = baos.toByteArray();
        }
    }

    @Test
    public void testAsync() {
        RestAssured
                .given()
                .when()
                .body(helloLambdaZipArchive)
                .post("/test/lambda/async")
                .then()
                .body(is(LambdaResource.OK));
    }

    @Test
    public void testBlocking() {
        RestAssured
                .given()
                .when()
                .body(helloLambdaZipArchive)
                .post("/test/lambda/blocking").then()
                .body(is(LambdaResource.OK));
    }
}
