/* global Java */

define(function () {
    var Scripts = Java.type('com.eas.script.Scripts');
    var FileClass = Java.type('java.io.File');
    var FileUtilsClass = Java.type('com.eas.util.FileUtils');
    var Paths = Java.type('java.nio.file.Paths');
    var AsynchronousFileChannel = Java.type('java.nio.channels.AsynchronousFileChannel');
    var StandardOpenOption = Java.type('java.nio.file.StandardOpenOption');

    function readString(aFileName, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        return FileUtilsClass.readString(new FileClass(aFileName), encoding);
    }

    function writeString(aFileName, aText, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        FileUtilsClass.writeString(new FileClass(aFileName), aText, encoding);
    }

    function pathsGet(aPathText) {
        return Paths.get(aPathText);
    }

    function Channel(aDelegate){
        this.size = function(){
            return aDelegate.size();
        };
        this.truncate = function(size){
            return new Channel(aDelegate.truncate(size));
        };
        this.force = function(metadata){
            aDelegate.force(metadata);
        };
        this.read = function(dst, position, onSuccess, onFailure){
            if (Scripts.getContext() != null) {
                Scripts.getContext().incAsyncsCount();
            }// else not in server environment
            aDelegate.read(dst, position, null, Scripts.asCompletionHandler(onSuccess, onFailure));
        };
        this.write = function(src, position, onSuccess, onFailure){
            if (Scripts.getContext() != null) {
                Scripts.getContext().incAsyncsCount();
            }// else not in server environment
            aDelegate.write(src, position, null, Scripts.asCompletionHandler(onSuccess, onFailure));
        };
    }
    /**
     * Returns the current size of this channel's file.
     *
     * @returns {Number} The current size of this channel's file, measured in bytes
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     * @throws  IOException
     *          If some other I/O error occurs
     */
    Channel.prototype.size = function(){};

    /**
     * Truncates this channel's file to the given size.
     *
     * <p> If the given size is less than the file's current size then the file
     * is truncated, discarding any bytes beyond the new end of the file.  If
     * the given size is greater than or equal to the file's current size then
     * the file is not modified. </p>
     *
     * @param  {Number} size
     *         The new size, a non-negative byte count
     *
     * @returns {Channel} This file channel
     *
     * @throws  NonWritableChannelException
     *          If this channel was not opened for writing
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  IllegalArgumentException
     *          If the new size is negative
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    Channel.prototype.truncate = function(size){};

    /**
     * Forces any updates to this channel's file to be written to the storage
     * device that contains it.
     *
     * <p> If this channel's file resides on a local storage device then when
     * this method returns it is guaranteed that all changes made to the file
     * since this channel was created, or since this method was last invoked,
     * will have been written to that device.  This is useful for ensuring that
     * critical information is not lost in the event of a system crash.
     *
     * <p> If the file does not reside on a local device then no such guarantee
     * is made.
     *
     * <p> The {@code metaData} parameter can be used to limit the number of
     * I/O operations that this method is required to perform.  Passing
     * {@code false} for this parameter indicates that only updates to the
     * file's content need be written to storage; passing {@code true}
     * indicates that updates to both the file's content and metadata must be
     * written, which generally requires at least one more I/O operation.
     * Whether this parameter actually has any effect is dependent upon the
     * underlying operating system and is therefore unspecified.
     *
     * <p> Invoking this method may cause an I/O operation to occur even if the
     * channel was only opened for reading.  Some operating systems, for
     * example, maintain a last-access time as part of a file's metadata, and
     * this time is updated whenever the file is read.  Whether or not this is
     * actually done is system-dependent and is therefore unspecified.
     *
     * <p> This method is only guaranteed to force changes that were made to
     * this channel's file via the methods defined in this class.
     *
     * @param   {Boolean} metaData
     *          If {@code true} then this method is required to force changes
     *          to both the file's content and metadata to be written to
     *          storage; otherwise, it need only force content changes to be
     *          written
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    Channel.prototype.force = function(metaData){};

    /**
     * Reads a sequence of bytes from this channel into the given buffer,
     * starting at the given file position.
     *
     * <p> This method initiates the reading of a sequence of bytes from this
     * channel into the given buffer, starting at the given file position. The
     * result of the read is the number of bytes read or {@code -1} if the given
     * position is greater than or equal to the file's size at the time that the
     * read is attempted.
     *
     * <p> This method works in the same manner as the {@link
     * AsynchronousByteChannel#read(ByteBuffer,Object,CompletionHandler)}
     * method, except that bytes are read starting at the given file position.
     * If the given file position is greater than the file's size at the time
     * that the read is attempted then no bytes are read.
     *
     * @param   {ByteBuffer} dst
     *          The buffer into which bytes are to be transferred
     * @param   {Number} position
     *          The file position at which the transfer is to begin;
     *          must be non-negative
     * @param   {Function} onSuccess
     *          The callback for consuming the read bytes count.
     * @param   {Function} onFailure
     *          The callback for consuming the failure cause.
     *
     * @throws  IllegalArgumentException
     *          If the position is negative or the buffer is read-only
     * @throws  NonReadableChannelException
     *          If this channel was not opened for reading
     */
    Channel.prototype.read = function(dst, position, onSuccess, onFailure){};

    /**
     * Writes a sequence of bytes to this channel from the given buffer, starting
     * at the given file position.
     *
     * <p> This method works in the same manner as the {@link
     * AsynchronousByteChannel#write(ByteBuffer,Object,CompletionHandler)}
     * method, except that bytes are written starting at the given file position.
     * If the given position is greater than the file's size, at the time that
     * the write is attempted, then the file will be grown to accommodate the new
     * bytes; the values of any bytes between the previous end-of-file and the
     * newly-written bytes are unspecified.
     *
     * @param   {ByteBuffer} src
     *          The buffer from which bytes are to be transferred
     * @param   {Number} position
     *          The file position at which the transfer is to begin;
     *          must be non-negative
     * @param   {Function} onSuccess
     *          The callback for consuming the number of bytes written.
     * @param   {Function} onFailure
     *          The callback for consuming the failure cause.
     *
     * @throws  IllegalArgumentException
     *          If the position is negative
     * @throws  NonWritableChannelException
     *          If this channel was not opened for writing
     */
    Channel.prototype.write = function(src, position, onSuccess, onFailure){};
    
    function openWrite(aPath){
        return new Channel(AsynchronousFileChannel.open(aPath, StandardOpenOption.WRITE));
    }
    function openTruncate(aPath){
        return new Channel(AsynchronousFileChannel.open(aPath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING));
    }
    function openAppend(aPath){
        return new Channel(AsynchronousFileChannel.open(aPath, StandardOpenOption.WRITE, StandardOpenOption.APPEND));
    }
    function create(aPath){
        return new Channel(AsynchronousFileChannel.open(aPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE));
    }
    function createNew(aPath){
        return new Channel(AsynchronousFileChannel.open(aPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW));
    }
    function openRead(aPath){
        return new Channel(AsynchronousFileChannel.open(aPath, StandardOpenOption.READ));
    }
        
    var module = {
        /**
         * Reads all contents of a file and returns it as a string.
         * @param {String} aFileName file absolute path.
         * @param {String} aEncoding Encoding name (e.g. "utf-8")
         * @returns String read from file.
         */
        read: function (aFileName, aEncoding) {},
        /**
         * Writes a string contents of a file.
         * @param {String} aFileName file absolute path.
         * @param {type} aText String to write to file.
         * @param {String} aEncoding Encoding name (e.g. "utf-8")
         * @returns {undefined}.
         */
        write: function (aFileName, aText, aEncoding) {},

        /**
         * Creates a new path object with paths relativize and resolve capabilities.
         * @param {type} aPathText Text, the new path object should be constructed upon.
         * @returns {object} Path object with relativize and resolve capabilities.
         */
        path: function (aPathText) {
            return {
                /**
                 * Returns the file system that created this object.
                 *
                 * @returns  {FileSystem} the file system that created this object
                 */
                getFileSystem: function(){},

                /**
                 * Tells whether or not this path is absolute.
                 *
                 * <p> An absolute path is complete in that it doesn't need to be combined
                 * with other path information in order to locate a file.
                 *
                 * @returns  {Boolean} {@code true} if, and only if, this path is absolute
                 */
                isAbsolute: function(){},

                /**
                 * Returns the root component of this path as a {@code Path} object,
                 * or {@code null} if this path does not have a root component.
                 *
                 * @returns  {Path} a path representing the root component of this path,
                 *          or {@code null}
                 */
                getRoot: function(){},

                /**
                 * Returns the name of the file or directory denoted by this path as a
                 * {@code Path} object. The file name is the <em>farthest</em> element from
                 * the root in the directory hierarchy.
                 *
                 * @returns  {Path} a path representing the name of the file or directory, or
                 *          {@code null} if this path has zero elements
                 */
                getFileName: function(){},

                /**
                 * Returns the <em>parent path</em>, or {@code null} if this path does not
                 * have a parent.
                 *
                 * <p> The parent of this path object consists of this path's root
                 * component, if any, and each element in the path except for the
                 * <em>farthest</em> from the root in the directory hierarchy. This method
                 * does not access the file system; the path or its parent may not exist.
                 * Furthermore, this method does not eliminate special names such as "."
                 * and ".." that may be used in some implementations. On UNIX for example,
                 * the parent of "{@code /a/b/c}" is "{@code /a/b}", and the parent of
                 * {@code "x/y/.}" is "{@code x/y}". This method may be used with the {@link
                 * #normalize normalize} method, to eliminate redundant names, for cases where
                 * <em>shell-like</em> navigation is required.
                 *
                 * <p> If this path has one or more elements, and no root component, then
                 * this method is equivalent to evaluating the expression:
                 * <blockquote><pre>
                 * subpath(0,&nbsp;getNameCount()-1);
                 * </pre></blockquote>
                 *
                 * @returns {Path} a path representing the path's parent
                 */
                getParent: function(){},

                /**
                 * Returns the number of name elements in the path.
                 *
                 * @returns  {Number} the number of elements in the path, or {@code 0} if this path
                 *          only represents a root component
                 */
                getNameCount: function(){},

                /**
                 * Returns a name element of this path as a {@code Path} object.
                 *
                 * <p> The {@code index} parameter is the index of the name element to return.
                 * The element that is <em>closest</em> to the root in the directory hierarchy
                 * has index {@code 0}. The element that is <em>farthest</em> from the root
                 * has index {@link #getNameCount count}{@code -1}.
                 *
                 * @param   {Number} index
                 *          the index of the element
                 *
                 * @returns  {Path} the name element
                 *
                 * @throws  IllegalArgumentException
                 *          if {@code index} is negative, {@code index} is greater than or
                 *          equal to the number of elements, or this path has zero name
                 *          elements
                 */
                getName: function(){},

                /**
                 * Returns a relative {@code Path} that is a subsequence of the name
                 * elements of this path.
                 *
                 * <p> The {@code beginIndex} and {@code endIndex} parameters specify the
                 * subsequence of name elements. The name that is <em>closest</em> to the root
                 * in the directory hierarchy has index {@code 0}. The name that is
                 * <em>farthest</em> from the root has index {@link #getNameCount
                 * count}{@code -1}. The returned {@code Path} object has the name elements
                 * that begin at {@code beginIndex} and extend to the element at index {@code
                 * endIndex-1}.
                 *
                 * @param   {Number} beginIndex
                 *          the index of the first element, inclusive
                 * @param   {Number} endIndex
                 *          the index of the last element, exclusive
                 *
                 * @returns  {Path} a new {@code Path} object that is a subsequence of the name
                 *          elements in this {@code Path}
                 *
                 * @throws  IllegalArgumentException
                 *          if {@code beginIndex} is negative, or greater than or equal to
                 *          the number of elements. If {@code endIndex} is less than or
                 *          equal to {@code beginIndex}, or larger than the number of elements.
                 */
                subpath: function(){},

                /**
                 * Tests if this path starts with a {@code Path}, constructed by converting
                 * the given path string, in exactly the manner specified by the {@link
                 * #startsWith(Path) startsWith(Path)} method. On UNIX for example, the path
                 * "{@code foo/bar}" starts with "{@code foo}" and "{@code foo/bar}". It
                 * does not start with "{@code f}" or "{@code fo}".
                 *
                 * @param   {Path|String} other
                 *          the given path string
                 *
                 * @returns  {Boolean} {@code true} if this path starts with the given path; otherwise
                 *          {@code false}
                 *
                 * @throws  InvalidPathException
                 *          If the path string cannot be converted to a Path.
                 */
                startsWith: function(){},

                /**
                 * Tests if this path ends with a {@code Path}, constructed by converting
                 * the given path string, in exactly the manner specified by the {@link
                 * #endsWith(Path) endsWith(Path)} method. On UNIX for example, the path
                 * "{@code foo/bar}" ends with "{@code foo/bar}" and "{@code bar}". It does
                 * not end with "{@code r}" or "{@code /bar}". Note that trailing separators
                 * are not taken into account, and so invoking this method on the {@code
                 * Path}"{@code foo/bar}" with the {@code String} "{@code bar/}" returns
                 * {@code true}.
                 *
                 * @param   {Path|String} other
                 *          the given path string
                 *
                 * @returns  {Boolean} {@code true} if this path ends with the given path; otherwise
                 *          {@code false}
                 *
                 * @throws  InvalidPathException
                 *          If the path string cannot be converted to a Path.
                 */
                endsWith: function(){},

                /**
                 * Returns a path that is this path with redundant name elements eliminated.
                 *
                 * <p> The precise definition of this method is implementation dependent but
                 * in general it derives from this path, a path that does not contain
                 * <em>redundant</em> name elements. In many file systems, the "{@code .}"
                 * and "{@code ..}" are special names used to indicate the current directory
                 * and parent directory. In such file systems all occurrences of "{@code .}"
                 * are considered redundant. If a "{@code ..}" is preceded by a
                 * non-"{@code ..}" name then both names are considered redundant (the
                 * process to identify such names is repeated until it is no longer
                 * applicable).
                 *
                 * <p> This method does not access the file system; the path may not locate
                 * a file that exists. Eliminating "{@code ..}" and a preceding name from a
                 * path may result in the path that locates a different file than the original
                 * path. This can arise when the preceding name is a symbolic link.
                 *
                 * @returns  {Path} the resulting path or this path if it does not contain
                 *          redundant name elements; an empty path is returned if this path
                 *          does have a root component and all name elements are redundant
                 *
                 * @see #getParent
                 * @see #toRealPath
                 */
                normalize: function(){},

                // -- resolution and relativization --

                /**
                 * Converts a given path string to a {@code Path} and resolves it against
                 * this {@code Path} in exactly the manner specified by the {@link
                 * #resolve(Path) resolve} method. For example, suppose that the name
                 * separator is "{@code /}" and a path represents "{@code foo/bar}", then
                 * invoking this method with the path string "{@code gus}" will result in
                 * the {@code Path} "{@code foo/bar/gus}".
                 *
                 * @param   {Path|String} other
                 *          the path string or a {@code Path} instance to resolve against this path
                 *
                 * @returns  {Path} the resulting path
                 *
                 * @throws  InvalidPathException
                 *          if the path string cannot be converted to a Path.
                 *
                 * @see FileSystem#getPath
                 */
                resolve: function(){},

                /**
                 * Converts a given path string to a {@code Path} and resolves it against
                 * this path's {@link #getParent parent} path in exactly the manner
                 * specified by the {@link #resolveSibling(Path) resolveSibling} method.
                 *
                 * @param   {Path|String} other
                 *          the path string or a {@code Path} instance to resolve against this path's parent
                 *
                 * @returns  {Path} the resulting path
                 *
                 * @throws  InvalidPathException
                 *          if the path string cannot be converted to a Path.
                 *
                 * @see FileSystem#getPath
                 */
                resolveSibling: function(){},

                /**
                 * Constructs a relative path between this path and a given path.
                 *
                 * <p> Relativization is the inverse of {@link #resolve(Path) resolution}.
                 * This method attempts to construct a {@link #isAbsolute relative} path
                 * that when {@link #resolve(Path) resolved} against this path, yields a
                 * path that locates the same file as the given path. For example, on UNIX,
                 * if this path is {@code "/a/b"} and the given path is {@code "/a/b/c/d"}
                 * then the resulting relative path would be {@code "c/d"}. Where this
                 * path and the given path do not have a {@link #getRoot root} component,
                 * then a relative path can be constructed. A relative path cannot be
                 * constructed if only one of the paths have a root component. Where both
                 * paths have a root component then it is implementation dependent if a
                 * relative path can be constructed. If this path and the given path are
                 * {@link #equals equal} then an <i>empty path</i> is returned.
                 *
                 * <p> For any two {@link #normalize normalized} paths <i>p</i> and
                 * <i>q</i>, where <i>q</i> does not have a root component,
                 * <blockquote>
                 *   <i>p</i><tt>.relativize(</tt><i>p</i><tt>.resolve(</tt><i>q</i><tt>)).equals(</tt><i>q</i><tt>)</tt>
                 * </blockquote>
                 *
                 * <p> When symbolic links are supported, then whether the resulting path,
                 * when resolved against this path, yields a path that can be used to locate
                 * the {@link Files#isSameFile same} file as {@code other} is implementation
                 * dependent. For example, if this path is  {@code "/a/b"} and the given
                 * path is {@code "/a/x"} then the resulting relative path may be {@code
                 * "../x"}. If {@code "b"} is a symbolic link then is implementation
                 * dependent if {@code "a/b/../x"} would locate the same file as {@code "/a/x"}.
                 *
                 * @param   other
                 *          the path to relativize against this path
                 *
                 * @returns  {Path} the resulting relative path, or an empty path if both paths are
                 *          equal
                 *
                 * @throws  IllegalArgumentException
                 *          if {@code other} is not a {@code Path} that can be relativized
                 *          against this path
                 */
                relativize: function(){},

                /**
                 * Returns a URI to represent this path.
                 *
                 * <p> This method constructs an absolute {@link URI} with a {@link
                 * URI#getScheme() scheme} equal to the URI scheme that identifies the
                 * provider. The exact form of the scheme specific part is highly provider
                 * dependent.
                 *
                 * <p> In the case of the default provider, the URI is hierarchical with
                 * a {@link URI#getPath() path} component that is absolute. The query and
                 * fragment components are undefined. Whether the authority component is
                 * defined or not is implementation dependent. There is no guarantee that
                 * the {@code URI} may be used to construct a {@link java.io.File java.io.File}.
                 * In particular, if this path represents a Universal Naming Convention (UNC)
                 * path, then the UNC server name may be encoded in the authority component
                 * of the resulting URI. In the case of the default provider, and the file
                 * exists, and it can be determined that the file is a directory, then the
                 * resulting {@code URI} will end with a slash.
                 *
                 * <p> The default provider provides a similar <em>round-trip</em> guarantee
                 * to the {@link java.io.File} class. For a given {@code Path} <i>p</i> it
                 * is guaranteed that
                 * <blockquote><tt>
                 * {@link Paths#get(URI) Paths.get}(</tt><i>p</i><tt>.toUri()).equals(</tt><i>p</i>
                 * <tt>.{@link #toAbsolutePath() toAbsolutePath}())</tt>
                 * </blockquote>
                 * so long as the original {@code Path}, the {@code URI}, and the new {@code
                 * Path} are all created in (possibly different invocations of) the same
                 * Java virtual machine. Whether other providers make any guarantees is
                 * provider specific and therefore unspecified.
                 *
                 * <p> When a file system is constructed to access the contents of a file
                 * as a file system then it is highly implementation specific if the returned
                 * URI represents the given path in the file system or it represents a
                 * <em>compound</em> URI that encodes the URI of the enclosing file system.
                 * A format for compound URIs is not defined in this release; such a scheme
                 * may be added in a future release.
                 *
                 * @returns  {URI} the URI representing this path
                 *
                 * @throws  java.io.IOError
                 *          if an I/O error occurs obtaining the absolute path, or where a
                 *          file system is constructed to access the contents of a file as
                 *          a file system, and the URI of the enclosing file system cannot be
                 *          obtained
                 *
                 * @throws  SecurityException
                 *          In the case of the default provider, and a security manager
                 *          is installed, the {@link #toAbsolutePath toAbsolutePath} method
                 *          throws a security exception.
                 */
                toUri: function(){},

                /**
                 * Returns a {@code Path} object representing the absolute path of this
                 * path.
                 *
                 * <p> If this path is already {@link Path#isAbsolute absolute} then this
                 * method simply returns this path. Otherwise, this method resolves the path
                 * in an implementation dependent manner, typically by resolving the path
                 * against a file system default directory. Depending on the implementation,
                 * this method may throw an I/O error if the file system is not accessible.
                 *
                 * @returns  {Path} a path object representing the absolute path
                 *
                 * @throws  java.io.IOError
                 *          if an I/O error occurs
                 * @throws  SecurityException
                 *          In the case of the default provider, a security manager
                 *          is installed, and this path is not absolute, then the security
                 *          manager's {@link SecurityManager#checkPropertyAccess(String)
                 *          checkPropertyAccess} method is invoked to check access to the
                 *          system property {@code user.dir}
                 */
                toAbsolutePath: function(){},

                /**
                 * Returns the <em>real</em> path of an existing file.
                 *
                 * <p> The precise definition of this method is implementation dependent but
                 * in general it derives from this path, an {@link #isAbsolute absolute}
                 * path that locates the {@link Files#isSameFile same} file as this path, but
                 * with name elements that represent the actual name of the directories
                 * and the file. For example, where filename comparisons on a file system
                 * are case insensitive then the name elements represent the names in their
                 * actual case. Additionally, the resulting path has redundant name
                 * elements removed.
                 *
                 * <p> If this path is relative then its absolute path is first obtained,
                 * as if by invoking the {@link #toAbsolutePath toAbsolutePath} method.
                 *
                 * <p> The {@code options} array may be used to indicate how symbolic links
                 * are handled. By default, symbolic links are resolved to their final
                 * target. If the option {@link LinkOption#NOFOLLOW_LINKS NOFOLLOW_LINKS} is
                 * present then this method does not resolve symbolic links.
                 *
                 * Some implementations allow special names such as "{@code ..}" to refer to
                 * the parent directory. When deriving the <em>real path</em>, and a
                 * "{@code ..}" (or equivalent) is preceded by a non-"{@code ..}" name then
                 * an implementation will typically cause both names to be removed. When
                 * not resolving symbolic links and the preceding name is a symbolic link
                 * then the names are only removed if it guaranteed that the resulting path
                 * will locate the same file as this path.
                 *
                 * @param   options
                 *          options indicating how symbolic links are handled
                 *
                 * @returns  {Path} an absolute path represent the <em>real</em> path of the file
                 *          located by this object
                 *
                 * @throws  IOException
                 *          if the file does not exist or an I/O error occurs
                 * @throws  SecurityException
                 *          In the case of the default provider, and a security manager
                 *          is installed, its {@link SecurityManager#checkRead(String) checkRead}
                 *          method is invoked to check read access to the file, and where
                 *          this path is not absolute, its {@link SecurityManager#checkPropertyAccess(String)
                 *          checkPropertyAccess} method is invoked to check access to the
                 *          system property {@code user.dir}
                 */
                toRealPath: function(){},

                /**
                 * Returns a {@link File} object representing this path. Where this {@code
                 * Path} is associated with the default provider, then this method is
                 * equivalent to returning a {File} object constructed with the
                 * {String} representation of this path.
                 *
                 * <p> If this path was created by invoking the {@code File} {@link
                 * File#toPath toPath} method then there is no guarantee that the {@code
                 * File} object returned by this method is {@link #equals equal} to the
                 * original {File}.
                 *
                 * @returns  {File} a file object representing this path
                 *
                 * @throws  UnsupportedOperationException
                 *          if this {@code Path} is not associated with the default provider
                 */
                toFile: function(){}
            };
        },
        /**
         * Opens file for write access.
         * 
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openWrite: function(path){return new Channel();},
        /**
         * If the file already exists and it is opened for {@link #WRITE}
         * access, then its length is truncated to 0. This option is ignored
         * if the file is opened only for {@link #READ} access.
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openTruncate: function(path){return new Channel();},
        /**
         * If the file is opened for {@link #WRITE} access then bytes will be written
         * to the end of the file rather than the beginning.
         *
         * <p> If the file is opened for write access by other programs, then it
         * is file system specific if writing to the end of the file is atomic.
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openAppend: function(path){return new Channel();},
        /**
         * Create a new file if it does not exist.
         * This option is ignored if the {@link #CREATE_NEW} option is also set.
         * The check for the existence of the file and the creation of the file
         * if it does not exist is atomic with respect to other file system
         * operations.
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        create: function(path){return new Channel();},
        /**
         * Create a new file, failing if the file already exists.
         * The check for the existence of the file and the creation of the file
         * if it does not exist is atomic with respect to other file system
         * operations.
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        createNew: function(path){return new Channel();},
        /**
         * Opens file for read access.
         * 
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openRead: function(path){return new Channel();}
    };
    Object.defineProperty(module, "read", {
        value: readString
    });
    Object.defineProperty(module, "write", {
        value: writeString
    });
    Object.defineProperty(module, "path", {
        value: pathsGet
    });
    Object.defineProperty(module, "openWrite", {
        value: openWrite
    });
    Object.defineProperty(module, "openTruncate", {
        value: openTruncate
    });
    Object.defineProperty(module, "openAppend", {
        value: openAppend
    });
    Object.defineProperty(module, "create", {
        value: create
    });
    Object.defineProperty(module, "createNew", {
        value: createNew
    });
    Object.defineProperty(module, "openRead", {
        value: openRead
    });
    return module;
});