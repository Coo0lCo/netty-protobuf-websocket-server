// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: CinemaService.proto

package cn.scbc.protobuf.demo.v1;

public interface JointMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:protobuf.demo.v1.JointMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 type = 1;</code>
   * @return The type.
   */
  int getType();

  /**
   * <code>repeated string otherMember = 2;</code>
   * @return A list containing the otherMember.
   */
  java.util.List<java.lang.String>
      getOtherMemberList();
  /**
   * <code>repeated string otherMember = 2;</code>
   * @return The count of otherMember.
   */
  int getOtherMemberCount();
  /**
   * <code>repeated string otherMember = 2;</code>
   * @param index The index of the element to return.
   * @return The otherMember at the given index.
   */
  java.lang.String getOtherMember(int index);
  /**
   * <code>repeated string otherMember = 2;</code>
   * @param index The index of the value to return.
   * @return The bytes of the otherMember at the given index.
   */
  com.google.protobuf.ByteString
      getOtherMemberBytes(int index);
}
