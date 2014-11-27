package cn.pptest.ppmock.stubbing;

import static java.util.Arrays.asList;
import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.Response;
import cn.pptest.ppmock.ResponseDefinition;
import cn.pptest.ppmock.common.FileSource;
import cn.pptest.ppmock.common.IdGenerator;
import cn.pptest.ppmock.common.Json;
import cn.pptest.ppmock.common.UniqueFilenameGenerator;
import cn.pptest.ppmock.common.VeryShortIdGenerator;
import cn.pptest.ppmock.matcher.RequestPattern;
import cn.pptest.ppmock.matcher.ValuePattern;
import cn.pptest.ppmock.monitor.RequestListener;

public class StubMappingJsonRecorder implements RequestListener {
	
	private final FileSource mappingsFileSource;
	private final FileSource filesFileSource;
	//private final Admin admin;
	private IdGenerator idGenerator;
	
	public StubMappingJsonRecorder(FileSource mappingsFileSource, FileSource filesFileSource) {
		this.mappingsFileSource = mappingsFileSource;
		this.filesFileSource = filesFileSource;
		//this.admin = admin;
		idGenerator = new VeryShortIdGenerator();
	}

	@Override
	public void requestReceived(Request request, Response response) {
        RequestPattern requestPattern = buildRequestPatternFrom(request);
		
		if (requestNotAlreadyReceived(requestPattern) && response.isFromProxy()) {
		    //notifier().info(String.format("Recording mappings for %s", request.getUrl()));
		    writeToMappingAndBodyFile(request, response, requestPattern);
		} else {
		    //notifier().info(String.format("Not recording mapping for %s as this has already been received", request.getUrl()));
		}
	}

   private RequestPattern buildRequestPatternFrom(Request request) {
      RequestPattern requestPattern = new RequestPattern(request.getMethod(), request.getUrl());
      String body = request.getBodyAsString();
      if (!body.isEmpty()) {
         ValuePattern bodyPattern = ValuePattern.equalTo(request.getBodyAsString());
         requestPattern.setBodyPatterns(asList(bodyPattern));
      }

      return requestPattern;
   }

   private void writeToMappingAndBodyFile(Request request, Response response, RequestPattern requestPattern) {
        String fileId = idGenerator.generate();
        String mappingFileName = UniqueFilenameGenerator.generate(request, "mapping", fileId);
        String bodyFileName = UniqueFilenameGenerator.generate(request, "body", fileId);
        ResponseDefinition responseToWrite = new ResponseDefinition();
        responseToWrite.setStatus(response.getStatus());
        responseToWrite.setBodyFileName(bodyFileName);

        if (response.getHeaders().size() > 0) {
            responseToWrite.setHeaders(response.getHeaders());
        }

        StubMapping mapping = new StubMapping(requestPattern, responseToWrite);
        
        filesFileSource.writeBinaryFile(bodyFileName, response.getBody());
        mappingsFileSource.writeTextFile(mappingFileName, Json.write(mapping));
    }

    private boolean requestNotAlreadyReceived(RequestPattern requestPattern) {
        //VerificationResult verificationResult = admin.countRequestsMatching(requestPattern);
        //verificationResult.assertRequestJournalEnabled();
        //return (verificationResult.getCount() <= 1);
    	return true;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

}
