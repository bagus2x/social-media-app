package bagus2x.sosmed.presentation.feed.components

// TODO: Make it flat, so it can have better performance
// SELECT id, (SELECT COUNT(*) FROM comment c WHERE c.parent_id = p.id) as total_replies_count, total_replies FROM comment p WHERE p.feed_id = 10 ORDER BY ID;
//@Composable
//fun NestedComments(
//    comments: List<Comment>,
//    indent: Int,
//    maxIndent: Int = 3,
//    onCommentClicked: (Comment) -> Unit,
//    onReplyClicked: (Comment) -> Unit,
//    onUrlClicked: ((String) -> Unit)? = null,
//    onHashtagClicked: ((String) -> Unit)? = null,
//    onMentionClicked: ((String) -> Unit)? = null,
//) {
//    comments.forEach { comment ->
//
//        var isNestedCommentsVisible by rememberSaveable { mutableStateOf(false) }
//        key(comment.id) {
//            Comment(
//                comment = comment,
//                indentation = indent,
//                showMoreVisible = !isNestedCommentsVisible && comment.totalReplies > 0,
//                onCommentClicked = { onCommentClicked(comment) },
//                onReplyClicked = {
//                    onReplyClicked(comment)
//                    if (indent < maxIndent) {
//                        isNestedCommentsVisible = true
//                    }
//                },
//                onShowMoreClicked = {
//                    if (indent < maxIndent) {
//                        isNestedCommentsVisible = true
//                    }
//                },
//                onUrlClicked = onUrlClicked,
//                onHashtagClicked = onHashtagClicked,
//                onMentionClicked = onMentionClicked
//            )
//            if (indent < maxIndent) {
//                AnimatedVisibility(
//                    visible = isNestedCommentsVisible,
//                    enter = expandVertically()
//                ) {
//                    Column {
//                        NestedComments(
//                            comments = comment.replies,
//                            indent = indent + 1,
//                            onCommentClicked = onCommentClicked,
//                            onReplyClicked = onReplyClicked,
//                            onUrlClicked = onUrlClicked,
//                            onHashtagClicked = onHashtagClicked,
//                            onMentionClicked = onMentionClicked
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//fun LazyListScope.comments(
//    comments: LazyPagingItems<Comment>,
//    initialIndent: Int = 0,
//    maxIndent: Int = 3,
//    onCommentClicked: (Comment) -> Unit,
//    onReplyClicked: (Comment) -> Unit,
//    onUrlClicked: ((String) -> Unit)? = null,
//    onHashtagClicked: ((String) -> Unit)? = null,
//    onMentionClicked: ((String) -> Unit)? = null,
//) {
//    items(
//        items = comments,
//        key = Comment::id
//    ) { comment ->
//        if (comment != null) {
//            Column(modifier = Modifier.animateItemPlacement()) {
//                var isNestedCommentsVisible by rememberSaveable { mutableStateOf(false) }
//                Comment(
//                    comment = comment,
//                    indentation = initialIndent,
//                    modifier = Modifier.fillMaxWidth(),
//                    showMoreVisible = !isNestedCommentsVisible && comment.totalReplies > 0,
//                    onCommentClicked = { onCommentClicked(comment) },
//                    onReplyClicked = {
//                        onReplyClicked(comment)
//                        isNestedCommentsVisible = true
//                    },
//                    onShowMoreClicked = { isNestedCommentsVisible = true }
//                )
//                AnimatedVisibility(
//                    visible = isNestedCommentsVisible,
//                    enter = expandVertically()
//                ) {
//                    Column {
//                        NestedComments(
//                            comments = comment.replies,
//                            indent = initialIndent + 1,
//                            maxIndent = maxIndent,
//                            onCommentClicked = onCommentClicked,
//                            onReplyClicked = onReplyClicked,
//                            onUrlClicked = onUrlClicked,
//                            onHashtagClicked = onHashtagClicked,
//                            onMentionClicked = onMentionClicked
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
