// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/api/annotations.proto

package com.google.api;

public final class AnnotationsProto {
  private AnnotationsProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
    registry.add(com.google.api.AnnotationsProto.http);
  }
  public static final int HTTP_FIELD_NUMBER = 72295728;
  /**
   * <pre>
   * See `HttpRule`.
   * </pre>
   *
   * <code>extend .google.protobuf.MethodOptions { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessageLite.GeneratedExtension<
      com.google.protobuf.DescriptorProtos.MethodOptions,
      com.google.api.HttpRule> http = com.google.protobuf.GeneratedMessageLite
          .newSingularGeneratedExtension(
        com.google.protobuf.DescriptorProtos.MethodOptions.getDefaultInstance(),
        com.google.api.HttpRule.getDefaultInstance(),
        com.google.api.HttpRule.getDefaultInstance(),
        null,
        72295728,
        com.google.protobuf.WireFormat.FieldType.MESSAGE,
        com.google.api.HttpRule.class);

  static {
  }

  // @@protoc_insertion_point(outer_class_scope)
}
