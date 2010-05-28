/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2009-2010, Jesse Eichar             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scalax.io

/**
 * A flag interface for indicating that the object
 * represents a filesystem dependent option for opening
 * a file. Typically several options are declared together.
 * <p>
 * The {@link OpenOption} object defines
 * several such options that are supported by most
 * filesystems.  The filesystem should define which options
 * are accepted
 * </p>
 * 
 * @author  Jesse Eichar
 * @since   1.0
 */
trait OpenOption {
    OpenOption.definedValues = this :: OpenOption.values
}

/**
 * Several options that are supported by most
 * filesystems.
 * 
 * @author  Jesse Eichar
 * @since   1.0
 */
object OpenOption {
  private var definedValues = List[OpenOption]()
  def values = definedValues
  /**
   * Append to an existing file.
   * A file will not be created if the file does not exist
   */
   case object APPEND extends OpenOption
  /**
   * Creating a file if it does not exist, but parent directories will not be created
   */
  case object CREATE extends OpenOption
  /**
   * Creating a new file and fail if the file already exists
   */
  case object CREATE_NEW extends OpenOption
  /**
   * Creating a new file and all parent directories
   */
  case object CREATE_FULL extends OpenOption
  /**
   * Delete file on close.
   * <p>
   * If this option is used then the best effort will be made
   * to delete the file when close is called.  If close is not called
   * then the file will be deleted on VM termination (if possible)
   * </p>
   */
  case object DELETE_ON_CLOSE extends OpenOption
  /**
   * Requires that every update to the file's content (but not metadata)
   * be written synchronously to the underlying storage device
   */
  case object DSYNC extends OpenOption
  /**
   * Open a file for read access
   */
  case object READ extends OpenOption
  /**
   * A hint to create a sparse file if used with {@link #CREATE_NEW}
   */
  case object SPARSE extends OpenOption
  /**
   * Requires that every update to the file's content or metadata be
   * written synchronously to the underlying storage device
   */
  case object SYNC extends OpenOption
  /**
   * If file exists and is opened for WRITE access then truncate the file to
   * 0 bytes.  Ignored if opened for READ access
   */
  case object TRUNCATE extends OpenOption
  /**
   * Open file for write access
   */
  case object WRITE extends OpenOption

  /**
   * Collection of options: {@link #CREATE}, {@link #TRUNCATE_EXISTING}, {@link #WRITE}
   */
  final val WRITE_TRUNCATE = List(CREATE_FULL, TRUNCATE, WRITE)
  /**
   * Collection of options: {@link #CREATE}, {@link #TRUNCATE_EXISTING}, {@link #WRITE}
   */
  final val READ_WRITE = List(READ, CREATE_FULL, TRUNCATE, WRITE)
  /**
   * Collection of options: {@link #CREATE}, {@link #APPEND}, {@link #WRITE}
   */
  final val WRITE_APPEND = List(CREATE_FULL, APPEND, WRITE)
  
}

/**
 * Flags an option as an options that declares how to deal with links
 * <p>
 * See LinkOption object for the common options
 * </p>
 * 
 * @author  Jesse Eichar
 * @since   1.0
 */
trait LinkOption

/**
 * Contains the common Link Options
 * 
 * @author  Jesse Eichar
 * @since   1.0
 */
object LinkOption {
  case object NOFOLLOW_LINKS extends LinkOption() with OpenOption with CopyOption {}
}

/**
 * Flags an option as an option that declares how a file should be copied
 * 
 * @author  Jesse Eichar
 * @since   1.0
 */
trait CopyOption
