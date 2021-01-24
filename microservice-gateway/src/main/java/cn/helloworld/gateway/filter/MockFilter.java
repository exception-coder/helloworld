package cn.helloworld.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhangkai
 */
@Slf4j
public class MockFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求request
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        // 获取响应response
        ServerHttpResponse serverHttpResponse = exchange.getResponse();

        // 请求方式
        HttpMethod method = serverHttpRequest.getMethod();
        // 请求来源地址
        InetSocketAddress address = serverHttpRequest.getRemoteAddress();
        // 请求 URL
        String requestRawPath = serverHttpRequest.getURI().getRawPath();
        // 请求参数
        MultiValueMap requestQueryParams = serverHttpRequest.getQueryParams();
        // 请求头
        HttpHeaders httpRequestHeaders = serverHttpRequest.getHeaders();
        // 请求体
        AtomicReference<String> stringAtomicReference = new AtomicReference<>();

        final Flux<DataBuffer> requestBody = serverHttpRequest.getBody();

        ServerHttpRequestDecorator requestDecorator = new ServerHttpRequestDecorator(serverHttpRequest) {

            @Override
            public Flux<DataBuffer> getBody() {
                final List<byte[]> bytesArr = Lists.newArrayList();

                requestBody.map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    bytesArr.add(bytes);
                    DataBufferUtils.release(dataBuffer);
                    log.info("请求体信息【{}】",new String(bytes));
                    return dataBuffer;
                }).doOnComplete(() -> {
                    if (!bytesArr.isEmpty()) {
                        byte[] requestBodyByteArr = bytesArr.stream().reduce((bytes1, bytes2) ->
                                ByteUtils.concatenate(bytes1, bytes2)
                        ).get();

                    };

                });
//                requestBody.subscribe();
                return requestBody;

            }
        };


        log.info("--------------------响应信息--------------------");
        DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();
        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(serverHttpResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {

                    log.info("--------------------请求信息--------------------");
                    // 打印请求头信息
                    log.info("请求头信息：" + httpRequestHeaders);
                    //打印请求详细信息
                    log.info("来源地址【{}】，请求URL【{}】，请求方式【{}】，请求参数【{}】",
                            address.getHostName() + ":" + address.getPort(),
                            requestRawPath, method.name(),
                            JSON.toJSONString(requestQueryParams.values().toArray()));


                    List<byte[]> byteArrList = Lists.newArrayList();
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    log.info("响应状态：" + this.getStatusCode());
                    log.info("响应头信息：" + this.getHeaders());

                    Flux<DataBuffer> dataBufferFlux = fluxBody.map((dataBuffer) -> {
                        // probably should reuse buffers
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        // 释放掉内存
                        DataBufferUtils.release(dataBuffer);
                        byteArrList.add(content);
                        return bufferFactory.wrap(content);
                    }).doOnComplete(() -> {
                        if (!byteArrList.isEmpty()) {
                            byte[] responseByteArr = byteArrList.stream().reduce((bytes1, bytes2) ->
                                    ByteUtils.concatenate(bytes1, bytes2)
                            ).get();
                            String responseResult = new String(responseByteArr, Charset.forName("UTF-8"));
                            log.info("响应参数：" + responseResult);
                        }
                    });

                    Mono<Void> voidMono = super.writeWith(dataBufferFlux);
                    return voidMono;
                    //super.writeWith(dataBufferFlux);
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };


        return Mono.from(chain.filter(exchange.mutate().request(requestDecorator).response(responseDecorator).build()));
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    public DataBuffer createDataBuffer(byte[] bytes) {
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer dataBuffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        dataBuffer.write(bytes);
        return dataBuffer;
    }

    public static void main(String[] args) {
        Flux<Integer> integerFlux = Flux.just(1,2,3,4,5).map(number -> {
            return number;
        });
        Mono.from(integerFlux.reduce((integer1, integer2) ->
                integer1 + integer2
        )).map(Integer::intValue);

        integerFlux.subscribe();
    }


}
