#
# Be sure to run `pod lib lint DataInjector.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'DataInjector'
  s.version          = '0.1.0'
  s.swift_version    = '5.0'
  s.summary          = 'Easily map and manipulate nested dictionaries.'

# This description is used to generate tags and improve search results.
#   * Think: What does it do? Why did you write it? What is the focus?
#   * Try to keep it short, snappy and to the point.
#   * Write the description between the DESC delimiters below.
#   * Finally, don't worry about the indent, CocoaPods strips it!

  s.description      = <<-DESC
Data injector is a project to easily manipulate dictionaries before being handled by the application. For example, to map JSON data of restful API responses into view models together with view content or to fix API responses.
                       DESC

  s.homepage         = 'https://github.com/crescentflare/DataInjector'
  # s.screenshots     = 'www.example.com/screenshots_1', 'www.example.com/screenshots_2'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'Crescent Flare Apps' => 'info@crescentflare.com' }
  s.source           = { :git => 'https://github.com/crescentflare/DataInjector.git', :tag => s.version.to_s }
  # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'

  s.ios.deployment_target = '8.0'

  s.source_files = 'DataInjectorIOS/DataInjector/Classes/**/*'
  
  # s.resource_bundles = {
  #   'DataInjector' => ['DataInjectorIOS/DataInjector/Assets/*.png']
  # }

  # s.public_header_files = 'Pod/Classes/**/*.h'
  # s.frameworks = 'UIKit', 'MapKit'
  # s.dependency 'AFNetworking', '~> 2.3'
end
